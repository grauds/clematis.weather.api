package org.clematis.weather.rest;

import org.clematis.weather.WeatherApplicationTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.hateoas.config.HypermediaRestTemplateConfigurer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings("EI_EXPOSE_REP")
public class HateoasApiTests extends WeatherApplicationTests {

    @LocalServerPort
    private int port;

    @Autowired(required = false)
    private HypermediaRestTemplateConfigurer restTemplateConfigurer;

    public RestTemplate getRestTemplateWithHalMessageConverter() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory("http://localhost:" + port));
        if (restTemplateConfigurer != null) {
            restTemplateConfigurer.registerHypermediaTypes(restTemplate);
        }
        return restTemplate;
    }
}
