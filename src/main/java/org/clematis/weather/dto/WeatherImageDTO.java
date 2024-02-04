package org.clematis.weather.dto;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * This projection carries date and name of the image file. Used for calendars
 *
 * @author Anton Troshin
 */
@Data
public class WeatherImageDTO implements Serializable {

    String path;

    Date date;
}
