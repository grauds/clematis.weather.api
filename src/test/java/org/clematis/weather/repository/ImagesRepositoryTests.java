package org.clematis.weather.repository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.clematis.weather.WeatherApplicationTests;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import jworkspace.weather.model.WeatherImage;

/**
 * @author Anton Troshin
 */
public class ImagesRepositoryTests extends WeatherApplicationTests {

    private static final String DATE_FORMAT = "yyyy-MM-dd";

    @Autowired
    private ImagesRepository imagesRepository;

    @Test
    void testImages() {
        Assertions.assertEquals(4, imagesRepository.count());
    }

    @Test
    public void findMonthImages() throws ParseException {
        List<WeatherImage> images = imagesRepository.getMonthImages(
            new SimpleDateFormat(DATE_FORMAT).parse("2022-07-17")
        );
        Assertions.assertEquals(2, images.size());
        Assertions.assertEquals("2022-07-11 09-58-21.HEIC", images.get(0).getPath());
        Assertions.assertEquals("2022-07-11 09:58:21.0", images.get(0).getDate().toString());
    }

    @Test
    public void findYearImages() throws ParseException {
        List<WeatherImage> images = imagesRepository.getYearImages(
            new SimpleDateFormat(DATE_FORMAT).parse("2022-07-17")
        );
        Assertions.assertEquals(2, images.size());
    }

}
