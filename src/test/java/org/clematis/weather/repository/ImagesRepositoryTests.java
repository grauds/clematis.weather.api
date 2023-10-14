package org.clematis.weather.repository;

import org.clematis.weather.WeatherApplicationTests;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Anton Troshin
 */
public class ImagesRepositoryTests extends WeatherApplicationTests {

    @Autowired
    private ImagesRepository imagesRepository;

    @Test
    void testImages() {
        Assertions.assertEquals(4, imagesRepository.count());
    }

}
