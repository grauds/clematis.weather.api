package org.clematis.weather.rest;

import jworkspace.weather.model.Observation;

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
}
