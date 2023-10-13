package org.clematis.weather.rest;

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

        ResponseEntity<PagedModel<WeatherImage>> images = getRestTemplateWithHalMessageConverter()
            .exchange("/api/images",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {});
        Assertions.assertEquals(HttpStatus.OK, images.getStatusCode());

    }
}
