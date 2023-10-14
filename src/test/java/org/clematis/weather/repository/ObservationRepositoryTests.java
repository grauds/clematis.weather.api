package org.clematis.weather.repository;

import org.clematis.weather.WeatherApplicationTests;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests for observation records repository
 */
public class ObservationRepositoryTests extends WeatherApplicationTests {

    @Autowired
    private ObservationRepository observationRepository;

    @Test
    void testObservations() {
        Assertions.assertEquals(2541, observationRepository.count());
    }


}
