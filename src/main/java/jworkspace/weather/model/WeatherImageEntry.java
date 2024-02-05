package jworkspace.weather.model;

import java.util.Date;

import org.springframework.data.rest.core.config.Projection;

/**
 * @author Anton Troshin
 */
@Projection(name = "WeatherImageEntry", types = WeatherImage.class)
public interface WeatherImageEntry {

    Date getDate();

    String getPath();
}
