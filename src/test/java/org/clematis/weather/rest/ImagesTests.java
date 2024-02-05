package org.clematis.weather.rest;

import java.util.HashMap;
import java.util.Map;

import jworkspace.weather.model.dto.WeatherImageDTO;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * @author Anton Troshin
 */
public class ImagesTests extends HateoasApiTests {

    @Test
    public void testDayImages() {
        HttpHeaders headers = new HttpHeaders();

        Map<String, String> uriParam = new HashMap<>();
        uriParam.put("date", "2022-07-12");

        ResponseEntity<WeatherImageDTO[]> images = getRestTemplateWithHalMessageConverter()
            .exchange("/images/byDay?day={date}",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {},
                uriParam);
        Assertions.assertEquals(HttpStatus.OK, images.getStatusCode());
        Assertions.assertNotNull(images.getBody());
        Assertions.assertEquals(1, images.getBody().length);
    }

    @Test
    public void testMonthlyImages() {
        HttpHeaders headers = new HttpHeaders();

        Map<String, String> uriParam = new HashMap<>();
        uriParam.put("date", "2022-07-17");

        ResponseEntity<WeatherImageDTO[]> images = getRestTemplateWithHalMessageConverter()
            .exchange("/images/byMonth?month={date}",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {},
                uriParam);
        Assertions.assertEquals(HttpStatus.OK, images.getStatusCode());
        Assertions.assertNotNull(images.getBody());
        Assertions.assertEquals(2, images.getBody().length);
    }

    @Test
    public void testYearlyImages() {
        HttpHeaders headers = new HttpHeaders();

        Map<String, String> uriParam = new HashMap<>();
        uriParam.put("date", "2022-07-17");

        ResponseEntity<WeatherImageDTO[]> images = getRestTemplateWithHalMessageConverter()
            .exchange("/images/byYear?year={date}",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {},
                uriParam);
        Assertions.assertEquals(HttpStatus.OK, images.getStatusCode());
        Assertions.assertNotNull(images.getBody());
        Assertions.assertEquals(2, images.getBody().length);
    }
}
