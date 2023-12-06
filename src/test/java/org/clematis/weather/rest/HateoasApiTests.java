package org.clematis.weather.rest;

import java.util.ArrayList;
import java.util.List;

import org.clematis.weather.WeatherApplicationTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.hateoas.server.mvc.TypeConstrainedMappingJackson2HttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;


@SuppressFBWarnings("EI_EXPOSE_REP")
public class HateoasApiTests extends WeatherApplicationTests {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    @Qualifier("halJacksonHttpMessageConverter")
    private TypeConstrainedMappingJackson2HttpMessageConverter halJacksonHttpMessageConverter;

    public TestRestTemplate getRestTemplateWithHalMessageConverter() {
        List<HttpMessageConverter<?>> existingConverters = testRestTemplate.getRestTemplate().getMessageConverters();
        List<HttpMessageConverter<?>> newConverters = new ArrayList<>();
        newConverters.add(halJacksonHttpMessageConverter);
        newConverters.addAll(existingConverters);
        testRestTemplate.getRestTemplate().setMessageConverters(newConverters);
        return testRestTemplate;
    }
}
