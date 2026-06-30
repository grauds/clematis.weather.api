package org.clematis.weather.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.format.annotation.DateTimeFormat;

import jworkspace.weather.model.WeatherImage;
import jworkspace.weather.model.WeatherImageKey;

@RepositoryRestResource(path = "images")
public interface ImagesRepository extends JpaRepository<WeatherImage, WeatherImageKey> {

    @RestResource(path = "getRandomDayImageWithFallback", rel = "getRandomDayImageWithFallback")
    @Query(value = """
        SELECT *, 1 AS is_exact FROM images WHERE DATE(date) = DATE(:date)
            UNION ALL SELECT *, 0 AS is_exact FROM images WHERE MONTH(date) = MONTH(:date)
                       AND DAYOFMONTH(date) = DAYOFMONTH(:date)
                       AND YEAR(date) != YEAR(:date)
        ORDER BY is_exact DESC, ABS(YEAR(date) - YEAR(:date)), RAND() LIMIT 1
        """,
        nativeQuery = true
    )
    Optional<WeatherImage> getRandomDayImageWithFallback(@Param("date") Date date);

    @RestResource(path = "getRandomDayImage", rel = "getRandomDayImage")
    @Query(value = """
        SELECT * FROM images WHERE DATE(date) = DATE(:date) ORDER BY RAND() LIMIT 1
        """, nativeQuery = true)
    Optional<WeatherImage> getRandomImageByDay(@Param("date") Date date);

    @RestResource(path = "getRandomMonthImage", rel = "getRandomMonthImage")
    @Query(value = """
        SELECT * FROM images WHERE EXTRACT(YEAR FROM date) = EXTRACT(YEAR FROM :date)
        AND EXTRACT(MONTH FROM date) = EXTRACT(MONTH FROM :date) ORDER BY RAND() LIMIT 1
        """,
        nativeQuery = true
    )
    Optional<WeatherImage> getRandomImageByMonth(@Param("date") Date date);

    @RestResource(path = "getRandomYearImage", rel = "getRandomYearImage")
    @Query(value = """
        SELECT * FROM images WHERE EXTRACT(YEAR FROM date) = EXTRACT(YEAR FROM :date)
        ORDER BY RAND() LIMIT 1
        """, nativeQuery = true
    )
    Optional<WeatherImage> getRandomImageByYear(@Param("date") Date date);

    /**
     * Finds images by a specific day.
     * Accessible at: /api/images/search/getDayImages?day=yyyy-MM-dd
     */
    @RestResource(path = "getDayImages", rel = "getDayImages")
    @Query(value = """
        SELECT * FROM images WHERE DATEDIFF(DATE(date), DATE(:day)) = 0
        """,
        countQuery = "SELECT COUNT(*) FROM images WHERE DATEDIFF(DATE(date), DATE(:day)) = 0",
        nativeQuery = true
    )
    List<WeatherImage> getDayImages(
        @Param("day") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date
    );

    /**
     * Finds images by a specific month and year.
     * Accessible at: /api/images/search/getMonthImages?month=yyyy-MM-dd
     */
    @RestResource(path = "getMonthImages", rel = "getMonthImages")
    @Query(value = """
        SELECT * FROM images WHERE YEAR(DATE(date)) = YEAR(DATE(:month)) AND MONTH(DATE(date)) = MONTH(DATE(:month))
        """,
        countQuery = """
        SELECT COUNT(*) FROM images WHERE YEAR(DATE(date))
        = YEAR(DATE(:month)) AND MONTH(DATE(date)) = MONTH(DATE(:month))
        """,
        nativeQuery = true
    )
    List<WeatherImage> getMonthImages(
        @Param("month") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date
    );

    /**
     * Finds images by a specific year.
     * Accessible at: /api/images/search/getYearImages?year=yyyy-MM-dd
     */
    @RestResource(path = "getYearImages", rel = "getYearImages")
    @Query(value = "SELECT * FROM images WHERE YEAR(DATE(date)) = YEAR(DATE(:year))",
        countQuery = "SELECT COUNT(*) FROM images WHERE YEAR(DATE(date)) = YEAR(DATE(:year))",
        nativeQuery = true
    )
    List<WeatherImage> getYearImages(
        @Param("year") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date
    );
}