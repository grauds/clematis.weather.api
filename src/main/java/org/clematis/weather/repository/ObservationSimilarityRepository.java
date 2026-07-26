package org.clematis.weather.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Repository;

import jworkspace.weather.model.ObservationProjection;
import jworkspace.weather.model.ObservationSimilarity;
import jworkspace.weather.model.ObservationSimilarityKey;

@Repository
public interface ObservationSimilarityRepository extends
    JpaRepository<ObservationSimilarity, ObservationSimilarityKey> {

    /**
     * Finds the closest weather matches for a specific target day across both sides of the matrix.
     * Optimized to avoid global file sorts on unindexed internal temporary tables.
     */
    @Query(value = """
            (SELECT
                station_id AS weatherStationId,
                date_b AS date,
                distance 
            FROM observation_similarities 
            WHERE station_id = :stationId AND date_a = :targetDate AND profile_name = :profile
            ORDER BY distance ASC 
            LIMIT :maxResults)
            
            UNION ALL 
            
            (SELECT 
                station_id AS weatherStationId, 
                date_a AS date, 
                distance 
            FROM observation_similarities 
            WHERE station_id = :stationId AND date_b = :targetDate AND profile_name = :profile
            ORDER BY distance ASC 
            LIMIT :maxResults)
            
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
