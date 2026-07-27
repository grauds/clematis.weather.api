package jworkspace.weather.service

import jworkspace.weather.similarity.ObservationFeatures
import jworkspace.weather.similarity.ObservationVPTree

import java.util.stream.Collectors

import jworkspace.weather.model.Observation
import jworkspace.weather.model.ObservationKey
import jworkspace.weather.model.ObservationSimilarity
import jworkspace.weather.model.ObservationSimilarityKey
import jworkspace.weather.parser.WeatherParser
import jworkspace.weather.similarity.ObservationSimilarityCalculator
import jworkspace.weather.similarity.SimilarityWeights

import org.hibernate.Session
import org.hibernate.StatelessSession
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Service component responsible for bulk importing historical weather data from CSV files.
 * <p>
 * This class processes historical weather dumps sequentially to avoid out-of-memory errors
 * and uses a Hibernate {@link StatelessSession} to optimize database insert performance
 * across both MySQL and H2 engines without platform-specific syntax conflicts.
 * It also computes cross-observation similarity metrics based on pre-defined weights.
 * <p>
 * Service component optimized for bulk importing weather data and matching observations
 * exclusively against days that possess associated weather images.
 */
class WeatherImporter {

    private static final Logger LOG = LoggerFactory.getLogger(WeatherImporter.class)

    private static final float MAX_DISTANCE_THRESHOLD = 50.0f
    private static final int MAX_PHOTO_MATCHES_PER_DAY = 3

    static int loadWeatherData(Session session) {
        if (session == null || !session.isOpen()) {
            return 0
        }

        List<String> files = [
                "27612.01.02.2005.01.02.2006.1.0.0.en.unic.00000000.csv",
                "27612.01.02.2006.01.02.2010.1.0.0.en.unic.00000000.csv",
                "27612.01.02.2010.01.02.2015.1.0.0.en.unic.00000000.csv",
                "27612.01.02.2015.01.02.2021.1.0.0.en.unic.00000000.csv",
                "27612.01.02.2021.01.02.2022.1.0.0.en.unic.00000000.csv",
                "27612.01.02.2022.01.02.2023.1.0.0.en.unic.00000000.csv",
                "27612.01.02.2023.01.02.2024.1.0.0.en.unic.00000000.csv",
                "27612.01.02.2024.01.02.2025.1.0.0.en.unic.00000000.csv",
                "27612.01.02.2025.01.02.2026.1.0.0.en.unic.00000000.csv",
                "27612.01.02.2026.04.07.2026.1.0.0.en.unic.00000000.csv"
        ]

        StatelessSession statelessSession = session.getSessionFactory().openStatelessSession()
        int totalObservationsProcessed = 0

        try {
            // =========================================================================
            // IMPORT ALL RAW WEATHER OBSERVATIONS FROM CSV
            // =========================================================================
            LOG.info("Starting raw weather data import pipeline...")

            LOG.info("Loading existing observation primary keys for deduplication...")
            Set<ObservationKey> existingKeys = statelessSession.createQuery(
                    "SELECT o.key FROM Observation o WHERE o.key.weatherStationId = :stationId", ObservationKey.class)
                    .setParameter("stationId", 27612)
                    .getResultStream()
                    .collect(Collectors.toSet())

            statelessSession.beginTransaction()

            for (String fileName : files) {
                LOG.info("Parsing file: ${fileName}")
                List<Observation> currentFileObservations = new WeatherParser(true, fileName).read()
                if (!currentFileObservations) {
                    continue
                }

                for (Observation observation : currentFileObservations) {
                    if (!existingKeys.contains(observation.getKey())) {
                        statelessSession.insert(observation)
                        existingKeys.add(observation.getKey() as ObservationKey)
                        totalObservationsProcessed++
                    }
                }
            }

            statelessSession.getTransaction().commit()
            LOG.info("Successfully imported ${totalObservationsProcessed} new unique observations.")

            // =========================================================================
            // CALCULATE MATRIX AGAINST PHOTO-COVERED TARGETS
            // =========================================================================

            LOG.info("Starting proximity calculation against photo-covered days...")
            statelessSession.beginTransaction()

            LOG.info("Extracting distinct photo dates from images database table...")
            List<Date> rawPhotoDates = statelessSession.createNativeQuery(
                    "SELECT DISTINCT date FROM images", Date.class
            ).getResultList()

            if (rawPhotoDates.isEmpty()) {
                LOG.warn("No entries found inside the images table. Skipping similarity processing.")
                statelessSession.getTransaction().commit()
                return totalObservationsProcessed
            }

            LOG.info("Loading photo-proximal station observations from database layer...")

            // Build a fast, lightweight Native SQL query string instead of HQL
            StringBuilder sql = new StringBuilder()
            sql.append("SELECT * FROM observations o WHERE o.weather_station_id = :stationId AND (")

            for (int i = 0; i < rawPhotoDates.size(); i++) {
                if (i > 0) sql.append(" OR ")
                // Target your exact column names in MySQL (e.g., station_id, date)
                sql.append("o.date BETWEEN :startPhoto_" + i + " AND :endPhoto_" + i)
            }
            sql.append(")")

            // Bind the Native Query directly to Observation Entity map structure
            var photoQuery = statelessSession.createNativeQuery(sql.toString(), Observation.class)
                    .setParameter("stationId", 27612)

            long ninetyMinutesMs = 90 * 60 * 1000L
            for (int i = 0; i < rawPhotoDates.size(); i++) {
                Date photoDate = rawPhotoDates.get(i)
                photoQuery.setParameter("startPhoto_" + i, new Date(photoDate.getTime() - ninetyMinutesMs))
                photoQuery.setParameter("endPhoto_" + i, new Date(photoDate.getTime() + ninetyMinutesMs))
            }

            List<Observation> photoCoveredObservations = photoQuery.getResultList()
            LOG.info("Matched ${photoCoveredObservations.size()} base weather observations within photo frames.")

            // =========================================================================
            // LOAD EXISTING SIMILARITY KEYS TO PREVENT RE-RUN DUPLICATION CRASHES
            // =========================================================================
            LOG.info("Loading existing matrix keys from database...")
            Set<String> processedPairs = statelessSession.createQuery(
                    "SELECT sim.id FROM ObservationSimilarity sim WHERE sim.id.stationId = :stationId AND sim.id.profileName = 'PHOTO'",
                    ObservationSimilarityKey.class)
                    .setParameter("stationId", 27612)
                    .getResultStream()
                    .map({ id -> "${id.dateA.getTime()}_${id.dateB.getTime()}" })
                    .collect(Collectors.toSet())

            // =========================================================================
            // GENERATE DISCRETE HIGH-DIMENSIONAL VP-TREE SEARCH CLUSTERS
            // =========================================================================
            LOG.info("Structuring regional search trees based on temporal profiles...")

            // Key: Season(0-3), Inner Key: DayTime(true/false) -> Target Spatial Search Tree
            Map<Integer, Map<Boolean, List<Observation>>> structuralBuckets = new HashMap<>()
            Map<Integer, Map<Boolean, ObservationVPTree>> spatialTreeGrid = new HashMap<>()

            for (int s = 0; s < 4; s++) {
                structuralBuckets.put(s, [(true): new ArrayList<>(), (false): new ArrayList<>()])
                spatialTreeGrid.put(s, new HashMap<>())
            }

            for (Observation photoObs : photoCoveredObservations) {
                ObservationFeatures feat = ObservationFeatures.of(photoObs)
                if (feat.dayOfYear() == null || feat.hourOfDay() == null) {
                    continue
                }

                // Group by Season
                int month = (int) (feat.dayOfYear() / 31) // Approximated map grouping metric
                int season = (month == 11 || month <= 1) ? 0 : (month <= 4) ? 1 : (month <= 7) ? 2 : 3
                boolean isDay = (feat.hourOfDay() >= 6 && feat.hourOfDay() < 18)

                structuralBuckets.get(season).get(isDay).add(photoObs)
            }

            // Build isolated search trees for each environment cluster
            for (int s = 0; s < 4; s++) {
                for (boolean isDay : [true, false]) {
                    List<Observation> clusterList = structuralBuckets.get(s).get(isDay)
                    if (!clusterList.isEmpty()) {
                        spatialTreeGrid.get(s).put(isDay, new ObservationVPTree(clusterList))
                    }
                }
            }

            // =========================================================================
            // EXECUTE SPEED-OF-LIGHT MATRICES MATCHING VIA VP-TREE QUERY ROUTINES
            // =========================================================================
            LOG.info("Processing 70,000+ data profiles natively via multi-dimensional index routing...")

            int totalRows = 70000 // Approximate benchmark for logging context
            int[] progressCounter = [0]
            int[] insertCounter = [0]
            long startTime = System.currentTimeMillis()

            statelessSession.createQuery(
                    "FROM Observation o WHERE o.key.weatherStationId = :stationId", Observation.class)
                    .setParameter("stationId", 27612)
                    .getResultStream()
                    .forEach({ Observation observation ->

                        // Periodic progress indicator
                        progressCounter[0]++
                        if (progressCounter[0] % 5000 == 0) {
                            long elapsed = System.currentTimeMillis() - startTime
                            double rowsPerSec = (progressCounter[0] / (elapsed / 1000.0))
                            LOG.info("Progress: ${progressCounter[0]} observations scanned... Saved ${insertCounter[0]} similarities. (${String.format('%.2f', rowsPerSec)} rows/sec)")
                        }

                        ObservationFeatures feat = ObservationFeatures.of(observation)
                        if (feat.dayOfYear() == null || feat.hourOfDay() == null) return

                        int month = (int) (feat.dayOfYear() / 31)
                        int season = (month == 11 || month <= 1) ? 0 : (month <= 4) ? 1 : (month <= 7) ? 2 : 3
                        boolean isDay = (feat.hourOfDay() >= 6 && feat.hourOfDay() < 18)

                        ObservationVPTree relevantTree = spatialTreeGrid.get(season).get(isDay)
                        if (relevantTree == null) return // No photos matching this configuration context

                        // Pulls closest options directly via logarithmic routing
                        List<Observation> closestPhotoObs = relevantTree.findNearest(
                                observation, MAX_PHOTO_MATCHES_PER_DAY, MAX_DISTANCE_THRESHOLD
                        )

                        for (Observation photoObs : closestPhotoObs) {
                            if (observation.key.date == photoObs.key.date) continue

                            Double distance = ObservationSimilarityCalculator.between(
                                    observation, photoObs, SimilarityWeights.PHOTO
                            )

                            Date dateA = observation.key.date.before(photoObs.key.date) ? observation.key.date : photoObs.key.date
                            Date dateB = observation.key.date.before(photoObs.key.date) ? photoObs.key.date : observation.key.date

                            // Dynamic Unique Token verification lookup to isolate collision issues
                            String pairLookupKey = "${dateA.getTime()}_${dateB.getTime()}"
                            if (processedPairs.contains(pairLookupKey)) {
                                continue
                            }

                            statelessSession.insert(new ObservationSimilarity(
                                    id: new ObservationSimilarityKey(
                                            stationId: observation.key.weatherStationId,
                                            dateA: dateA,
                                            dateB: dateB,
                                            profileName: "PHOTO"
                                    ),
                                    distance: distance
                            ))
                            // Add pair lookup to runtime tracker cache to avoid self-collisions inside the stream
                            processedPairs.add(pairLookupKey)
                            insertCounter[0]++
                        }
                    })

            statelessSession.getTransaction().commit()
            long totalElapsed = System.currentTimeMillis() - startTime
            LOG.info("Pipeline executed successfully! Total scanned: ${progressCounter[0]}, Total inserted: ${insertCounter[0]} in ${totalElapsed / 1000.0} seconds.")
        } catch (Exception e) {
            if (statelessSession.getTransaction().isActive()) statelessSession.getTransaction().rollback()
            LOG.error("Pipeline failure: ", e)
            throw e
        } finally {
            statelessSession.close()
        }
        return totalObservationsProcessed
    }
}