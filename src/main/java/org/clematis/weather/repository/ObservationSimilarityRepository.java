package org.clematis.weather.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;
import jworkspace.weather.model.ObservationProjection;
import jworkspace.weather.model.ObservationSimilarity;
import jworkspace.weather.model.ObservationSimilarityKey;

@Repository
public interface ObservationSimilarityRepository extends
    JpaRepository<ObservationSimilarity, ObservationSimilarityKey> {

    @Modifying
    @Transactional
    @Query(value = """
            INSERT INTO observation_similarities (station_id, date_a, date_b, profile_name, distance)
            VALUES (:stationId, :dateA, :dateB, :profile, :distance)
            ON DUPLICATE KEY UPDATE distance = VALUES(distance)
        """, nativeQuery = true)
    void upsertSimilarity(
        @Param("stationId") Integer stationId,
        @Param("dateA") Date dateA,
        @Param("dateB") Date dateB,
        @Param("profile") String profile,
        @Param("distance") Float distance
    );

    /**
     * Finds the closest weather matches for a specific target day across both sides of the matrix.
     */
    @Query(value = """
            SELECT
                station_id AS weatherStationId,
                date_b AS date, 
                distance 
            FROM observation_similarities 
            WHERE station_id = :stationId AND date_a = :targetDate AND profile_name = :profile
            
            UNION ALL 
            
            SELECT 
                station_id AS weatherStationId, 
                date_a AS date, 
                distance 
            FROM observation_similarities 
            WHERE station_id = :stationId AND date_b = :targetDate AND profile_name = :profile
            
            ORDER BY distance ASC 
            LIMIT :maxResults
        """, nativeQuery = true)
    List<ObservationProjection> findSimilarObservations(
        @Param("stationId") Integer stationId,
        @Param("targetDate") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date dateTime,
        @Param("profile") String profile,
        @Param("maxResults") int maxResults
    );
}
