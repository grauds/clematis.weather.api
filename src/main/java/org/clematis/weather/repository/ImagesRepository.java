package org.clematis.weather.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.format.annotation.DateTimeFormat;

import jworkspace.weather.model.WeatherImage;
import jworkspace.weather.model.WeatherImageKey;

/**
 * @author Anton Troshin
 */
@RepositoryRestResource(path = "images")
public interface ImagesRepository extends PagingAndSortingRepository<WeatherImage, WeatherImageKey> {

    @Query(value = "select * from images where datediff(DATE(date), DATE(:day)) = 0",
           countQuery = "select count(*) from images where datediff(DATE(date), DATE(:day) = 0",
           nativeQuery = true
    )
    List<WeatherImage> getDayImages(@Param("day")
                                    @DateTimeFormat(pattern = "yyyy-MM-dd")
                                    Date date);

    @Query(value = "select * from images where YEAR(date(date))=YEAR(DATE(:month)) "
                   + "AND MONTH(date(date))=MONTH(DATE(:month))",
          countQuery = "select count(*) from images where YEAR(date(date))=YEAR(DATE(:month)) "
                   + "AND MONTH(date(date))=MONTH(DATE(:month))",
          nativeQuery = true
    )
    List<WeatherImage> getMonthImages(@Param("month")
                                      @DateTimeFormat(pattern = "yyyy-MM-dd")
                                      Date date);

    @Query(value = "select * from images where YEAR(date(date))=YEAR(DATE(:year))",
        countQuery = "select count(*) from images where YEAR(date(date))=YEAR(DATE(:year))",
        nativeQuery = true
    )
    List<WeatherImage> getYearImages(@Param("year")
                                     @DateTimeFormat(pattern = "yyyy-MM-dd")
                                     Date date);
}
