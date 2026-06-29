package org.clematis.weather.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.format.annotation.DateTimeFormat;

import jworkspace.weather.model.Observation;
import jworkspace.weather.model.ObservationKey;

/**
 * @author Anton Troshin
 */
@RepositoryRestResource(path = "observations")
public interface ObservationRepository extends JpaRepository<Observation, ObservationKey> {
   /**
     * Finds observations by station, day, and hour.
     * Accessible at: /api/observations/search/findByStationDayAndHour
     */
    @RestResource(path = "findByStationDayAndHour", rel = "findByStationDayAndHour")
    @Query("""
            SELECT o FROM Observation o WHERE o.key.weatherStationId = :stationId
            AND FUNCTION('DATE', o.key.date) = FUNCTION('DATE', :dateTime)
            AND FUNCTION('HOUR', o.key.date) = FUNCTION('HOUR', :dateTime)
        """)
    List<Observation> findByStationDayAndHour(
        @Param("stationId") Integer stationId,
        @Param("dateTime") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date dateTime
    );

}
