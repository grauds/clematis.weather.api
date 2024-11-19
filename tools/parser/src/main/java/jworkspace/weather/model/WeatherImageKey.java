package jworkspace.weather.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author Anton Troshin
 */
@Embeddable
@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class WeatherImageKey implements Serializable {

    @Serial
    private static final long serialVersionUID = -3952895677364699023L;

    private Date date;

    private String path;
}
