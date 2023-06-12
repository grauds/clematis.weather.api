package jworkspace.weather;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Embeddable;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

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
public class ObservationKey  implements Serializable {

    private static final long serialVersionUID = -3952895633364699023L;
    /**
     * Weather station unique id (27612 for VDNH)
     */
    private Integer weatherStationId;
    /**
     * Local time in this location. Summer (Daylight Saving Time) is taken into consideration
     */
    @SuppressFBWarnings("EI_EXPOSE_REP")
    private Date date;
}
