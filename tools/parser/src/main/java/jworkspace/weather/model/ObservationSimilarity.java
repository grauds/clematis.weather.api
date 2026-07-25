package jworkspace.weather.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@Table(name = "observation_similarities")
@ToString
public class ObservationSimilarity {

    @EmbeddedId
    private ObservationSimilarityKey id = new ObservationSimilarityKey();

    @Column(name = "distance", nullable = false)
    private Float distance;

    /*
     * Optional Relationship Maps:
     * Uncomment these only if your application relies heavily on graph navigation.
     * For high-throughput batch inserts or math processing, using the IDs
     * inside the 'id' field is significantly faster.
     */
    /*
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "station_id", referencedColumnName = "weather_station_id", insertable = false, updatable = false),
        @JoinColumn(name = "date_a", referencedColumnName = "date", insertable = false, updatable = false)
    })
    private Observation observationA;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "station_id", referencedColumnName = "weather_station_id", insertable = false, updatable = false),
        @JoinColumn(name = "date_b", referencedColumnName = "date", insertable = false, updatable = false)
    })
    private Observation observationB;
    */
}