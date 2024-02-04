package org.clematis.weather.dto;

import java.io.Serializable;
import java.util.Date;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Data;

/**
 * This projection carries date and name of the image file. Used for calendars
 *
 * @author Anton Troshin
 */
@Data
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class WeatherImageDTO implements Serializable {

    String path;

    Date date;
}
