package org.clematis.weather.rest;

import java.util.HashMap;
import java.util.Map;
import jworkspace.weather.model.WeatherImage;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ImagesTests extends HateoasApiTests {

    @Test
    public void testImages() {
        HttpHeaders headers = new HttpHeaders();

        Map<String, String> uriParam = new HashMap<>();
        uriParam.put("date", "2022-07-17");

        ResponseEntity<PagedModel<WeatherImage>> images = getRestTemplateWithHalMessageConverter()
            .exchange("/api/images/search/byDay?date={date}",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {},
                uriParam);
        Assertions.assertEquals(HttpStatus.OK, images.getStatusCode());

    }

    @Test
    public void testMothlyImages() {
        HttpHeaders headers = new HttpHeaders();

        Map<String, String> uriParam = new HashMap<>();
        uriParam.put("date", "2022-07-17");

        ResponseEntity<PagedModel<WeatherImage>> images = getRestTemplateWithHalMessageConverter()
            .exchange("/api/images/search/byMonth?month={date}",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {},
                uriParam);
        Assertions.assertEquals(HttpStatus.OK, images.getStatusCode());

    }

    @Test
    public void testYearlyImages() {
        HttpHeaders headers = new HttpHeaders();

        Map<String, String> uriParam = new HashMap<>();
        uriParam.put("date", "2022-07-17");

        ResponseEntity<PagedModel<WeatherImage>> images = getRestTemplateWithHalMessageConverter()
            .exchange("/api/images/search/byYear?year={date}",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {},
                uriParam);
        Assertions.assertEquals(HttpStatus.OK, images.getStatusCode());

    }
}
