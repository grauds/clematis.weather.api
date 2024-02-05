package jworkspace.weather.model.dto;

import java.io.Serializable;
import java.util.Date;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Data;

/**
 * @author Anton Troshin
 */
@Data
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class WeatherImageDTO implements Serializable {

    String path;

    Date date;
}
