package org.clematis.weather.repository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.clematis.weather.WeatherApplicationTests;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import jworkspace.weather.model.Observation;
/**
 * Tests for observation records repository
 */
public class ObservationRepositoryTests extends WeatherApplicationTests {

    private static final String DATE_FORMAT = "yyyy-MM-dd";

    @Autowired
    private ObservationRepository observationRepository;

    @Test
    void testObservations() {
        Assertions.assertEquals(2541, observationRepository.count());
    }

    @Test
    public void findDayObservations() throws ParseException {
        List<Observation> observations = observationRepository.getDayObservations(
            new SimpleDateFormat(DATE_FORMAT).parse("2005-02-02")
        );
        Assertions.assertEquals(7, observations.size());
    }
}
