package org.clematis.weather.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.format.annotation.DateTimeFormat;

import jworkspace.weather.model.Observation;
import jworkspace.weather.model.ObservationKey;

/**
 * @author Anton Troshin
 */
@RepositoryRestResource(path = "observations")
public interface ObservationRepository extends PagingAndSortingRepository<Observation, ObservationKey> {

    @Query(value = "select * from observations where datediff(DATE(date), DATE(:day)) = 0",
           countQuery = "select count(*) from observations where datediff(DATE(date), DATE(:day)) = 0",
           nativeQuery = true
    )
    List<Observation> getDayObservations(@Param("day")
                                         @DateTimeFormat(pattern = "yyyy-MM-dd")
                                         Date date);
}
