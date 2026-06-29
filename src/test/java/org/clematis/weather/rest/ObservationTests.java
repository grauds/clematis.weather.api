package org.clematis.weather.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jworkspace.weather.model.Observation;
import jworkspace.weather.model.dto.WeatherImageDTO;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ObservationTests extends HateoasApiTests {

    @Test
    public void testObservations() {
        HttpHeaders headers = new HttpHeaders();

        ResponseEntity<PagedModel<Observation>> accountsTotals = getRestTemplateWithHalMessageConverter()
            .exchange("/api/observations",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {});
        Assertions.assertEquals(HttpStatus.OK, accountsTotals.getStatusCode());
    }

    @Test
    public void testDayObservations() {
        HttpHeaders headers = new HttpHeaders();

        Map<String, String> uriParam = new HashMap<>();
        uriParam.put("dateTime", "2005-02-02 09:00:00");
        uriParam.put("stationId", "27612");
//api/observations/search/findByStationDayAndHour?dateTime=2025-06-23 09:00:00&stationId=27612
        ResponseEntity<PagedModel<WeatherImageDTO>> images = getRestTemplateWithHalMessageConverter()
            .exchange("/api/observations/search/findByStationDayAndHour?dateTime={dateTime}&stationId={stationId}",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {},
                uriParam);
        Assertions.assertEquals(HttpStatus.OK, images.getStatusCode());
        Assertions.assertNotNull(images.getBody());
        Assertions.assertEquals(1, images.getBody().getContent().size());
    }
}
