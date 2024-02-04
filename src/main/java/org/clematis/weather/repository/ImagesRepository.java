package org.clematis.weather.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import jworkspace.weather.model.WeatherImage;
import jworkspace.weather.model.WeatherImageKey;

/**
 * @author Anton Troshin
 */
@RepositoryRestResource(path = "images")
public interface ImagesRepository extends PagingAndSortingRepository<WeatherImage, WeatherImageKey> {

    @Query(value = "select path from images where datediff(date, DATE(:date)) = 0",
           countQuery = "select count(*) from images where datediff(date, DATE(:date)) = 0",
           nativeQuery = true
    )
    List<String> getImages(@Param("date") Date date);

    @Query(value = "select date, path from images where YEAR(date(date))=YEAR(date(DATE(:month))) "
                   + "AND MONTH(date(date))=MONTH(date(DATE(:month)))",
          countQuery = "select count(*) from images where YEAR(date(date))=YEAR(date(DATE(:month))) "
                   + "AND MONTH(date(date))=MONTH(date(DATE(:month)))",
          nativeQuery = true
    )
    @RestResource(path = "byMonth")
    List<IDateAndPath> getMonthImages(@Param("month") Date date);

    @Query(value = "select date, path from images where YEAR(date(date))=YEAR(date(DATE(:year)))",
        countQuery = "select count(*) from images where YEAR(date(date))=YEAR(date(DATE(:year)))",
        nativeQuery = true
    )
    @RestResource(path = "byYear")
    List<IDateAndPath> getYearImages(@Param("year") Date date);
}
