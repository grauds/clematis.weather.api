package jworkspace.weather.model;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class ObservationSimilarityKey implements Serializable {

    @Column(name = "station_id", nullable = false)
    private Integer stationId;

    @Column(name = "date_a", nullable = false)
    private Date dateA;

    @Column(name = "date_b", nullable = false)
    private Date dateB;

    @SuppressWarnings("checkstyle:MagicNumber")
    @Column(name = "profile_name", nullable = false, length = 30)
    private String profileName;
}
