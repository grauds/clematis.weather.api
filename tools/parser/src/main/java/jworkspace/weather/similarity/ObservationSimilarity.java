package jworkspace.weather.similarity;
/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 2026 Anton Troshin

   This file is part of Java Workspace.

   This application is free software; you can redistribute it and/or
   modify it under the terms of the GNU Library General Public
   License as published by the Free Software Foundation; either
   version 2 of the License, or (at your option) any later version.

   This application is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   Library General Public License for more details.

   You should have received a copy of the GNU Library General Public
   License along with this application; if not, write to the Free
   Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

   The author may be contacted at:

   anton.troshin@gmail.com
  ----------------------------------------------------------------------------
*/

import java.time.LocalDateTime;
import java.time.ZoneId;

import jworkspace.weather.model.Observation;

/**
 * Calculates the total mathematical distance between two weather observations.
 * <p>
 * This class maps the domain fields from an {@link Observation} to the properties
 * consumed by {@link SimilarityFeature}, then computes a weighted average distance.
 * It tolerates data gaps by ignoring missing features and dividing by the total weight
 * of the features that were actually evaluated.
 * </p>
 */
public final class ObservationSimilarity {

    private ObservationSimilarity() {}

    /**
     * @param first   the first weather observation, never null
     * @param second  the second weather observation, never null
     * @param weights the weighting configuration to apply, never null
     * @return the total distance between {@code 0} (identical) and {@code 1} (completely different),
     *         or null when there are no matching features to compare
     */
    public static Double between(Observation first, Observation second, SimilarityWeights weights) {

        ObservationFeatures featuresFirst = extractFeatures(first);
        ObservationFeatures featuresSecond = extractFeatures(second);

        double accumulatedDistance = 0.0;
        double accumulatedWeight = 0.0;

        for (SimilarityFeature feature : SimilarityFeature.values()) {
            double weight = weights.weightOf(feature);
            if (weight <= 0.0) {
                continue;
            }

            Double distance = feature.difference(featuresFirst, featuresSecond);
            if (distance != null) {
                accumulatedDistance += distance * weight;
                accumulatedWeight += weight;
            }
        }

        return accumulatedWeight == 0.0 ? null : accumulatedDistance / accumulatedWeight;
    }

    private static ObservationFeatures extractFeatures(Observation observation) {
        if (observation == null) {
            return new ObservationFeatures(
                null, null, null, null, null, null, null, null, null, null, null, null, null
            );
        }

        Float temperature = observation.getT() == null ? null : observation.getT();
        Float humidity = observation.getU() == null ? null : observation.getU();
        Float pressure = observation.getP() == null ? null : observation.getP();
        Float visibility = observation.getVv() == null ? null : observation.getVv();
        Float precipitation = observation.getRrr() == null ? null : observation.getRrr();
        Float snowDepth = observation.getSss() == null ? null : observation.getSss();

        Float dewPointSpread = null;
        if (observation.getT() != null && observation.getTd() != null) {
            dewPointSpread = observation.getT() - observation.getTd();
        }

        Float windSpeed = parseWindSpeed(observation.getFf());
        Float windBearing = WindDirections.degrees(observation.getDd());
        Float cloudCover = parseCloudCover(observation.getN());

        WeatherPhenomenon phenomenon = WeatherPhenomenon.of(observation.getWw());

        int dayOfYear = 0;
        int hourOfDay = 0;
        if (observation.getKey() != null && observation.getKey().getDate() != null) {
            LocalDateTime dateTime = observation.getKey().getDate().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
            dayOfYear = dateTime.getDayOfYear();
            hourOfDay = dateTime.getHour();
        }

        return new ObservationFeatures(
            temperature,
            dewPointSpread,
            humidity,
            pressure,
            cloudCover,
            windSpeed,
            windBearing,
            visibility,
            precipitation,
            snowDepth,
            phenomenon,
            dayOfYear,
            hourOfDay
        );
    }

    private static Float parseWindSpeed(String ff) {
        if (ff != null && !ff.isBlank()) {
            try {
                return Float.parseFloat(ff.trim());
            } catch (NumberFormatException _) {}
        }
        return null;
    }

    private static Float parseCloudCover(String n) {
        return CloudAmount.fraction(n);
    }
}
