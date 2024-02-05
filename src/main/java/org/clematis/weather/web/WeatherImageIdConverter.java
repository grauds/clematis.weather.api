package org.clematis.weather.web;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.springframework.data.rest.webmvc.spi.BackendIdConverter;
import org.springframework.stereotype.Component;

import jworkspace.weather.model.WeatherImageKey;

/**
 * @author Anton Troshin
 */
@Component
public class WeatherImageIdConverter implements BackendIdConverter {

    public static final String DELIMITER = "_";

    public static final int PARTS = 2;

    private static final String DATE_FORMAT = "yyyy-MM-dd";

    @Override
    public Serializable fromRequestId(String id, Class<?> entityType) {
        String[] parts = id.split(DELIMITER, PARTS);
        int i = 0;
        try {
            if (parts.length == PARTS) {
                return new WeatherImageKey(new SimpleDateFormat(DATE_FORMAT).parse(parts[i]), parts[++i]);
            } else {
                throw new ParseException("Bad id format", 0);
            }
        } catch (ParseException | NumberFormatException e) {
            throw new IllegalArgumentException("Id must consist of a date and an path separated by '_'", e);
        }
    }

    @Override
    public String toRequestId(Serializable id, Class<?> entityType) {
        WeatherImageKey key = (WeatherImageKey) id;
        return new SimpleDateFormat(DATE_FORMAT).format(key.getDate()) + DELIMITER + key.getPath();
    }

    @Override
    public boolean supports(Class<?> delimiter) {
        return delimiter.isAssignableFrom(WeatherImageKey.class);
    }
}
