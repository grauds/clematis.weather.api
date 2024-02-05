package org.clematis.weather.web;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.springframework.data.rest.webmvc.spi.BackendIdConverter;

import jworkspace.weather.model.ObservationKey;

/**
 * @author Anton Troshin
 */
public class ObservationIdConverter implements BackendIdConverter {

    public static final String DELIMITER = "_";

    public static final int PARTS = 2;

    private static final String DATE_FORMAT = "yyyy-MM-dd";

    @Override
    public Serializable fromRequestId(String id, Class<?> entityType) {
        String[] parts = id.split(DELIMITER, PARTS);
        int i = 0;
        try {
            if (parts.length == PARTS) {
                return new ObservationKey(Integer.parseInt(parts[i]),
                    new SimpleDateFormat(DATE_FORMAT).parse(parts[++i]));
            } else {
                throw new ParseException("Bad id format", 0);
            }
        } catch (ParseException | NumberFormatException e) {
            throw new IllegalArgumentException(
                "Id must consist of a date and an weather station id separated by '_'", e
            );
        }
    }

    @Override
    public String toRequestId(Serializable id, Class<?> entityType) {
        ObservationKey key = (ObservationKey) id;
        return new SimpleDateFormat(DATE_FORMAT).format(key.getDate()) + DELIMITER + key.getWeatherStationId();
    }

    @Override
    public boolean supports(Class<?> delimiter) {
        return delimiter.isAssignableFrom(ObservationKey.class);
    }
}
