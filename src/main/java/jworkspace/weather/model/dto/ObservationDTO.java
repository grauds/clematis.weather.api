package jworkspace.weather.model.dto;

import java.util.Date;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jworkspace.weather.model.WindDirection;
import lombok.Data;

/**
 * @author Anton Troshin
 */
@Data
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class ObservationDTO {

    /**
     * Local time in this location. Summer (Daylight Saving Time) is taken into consideration
     */
    private Date date;
    /**
     * Air temperature (degrees Celsius) at 2 metre height above the earth's surface
     */
    private Float t;
    /**
     * Atmospheric pressure at weather station level (millimeters of mercury)
     */
    private Float pO;
    /**
     * Atmospheric pressure reduced to mean sea level (millimeters of mercury)
     */
    private Float p;
    /**
     * Pressure tendency: changes in atmospheric pressure over the last three hours (millimeters of mercury)
     */
    private Float pA;
    /**
     * Relative humidity (%) at a height of 2 metres above the earth's surface
     */
    private Float u;
    /**
     * Mean wind direction (compass points) at a height of 10-12 metres
     * above the earth’s surface over the 10-minute period immediately preceding the observation
     */
    @Enumerated(EnumType.STRING)
    private WindDirection dd;
    /**
     * Mean wind speed at a height of 10-12 metres above the earth’s surface over the
     * 10-minute period immediately preceding the observation (meters per second)
     */
    private String ff;
    /**
     * Maximum gust value at a height of 10-12 metres above the earth’s surface over the 10-minute
     * period immediately preceding the observation (meters per second)
     */
    private String ff10;
    /**
     * Maximum gust value at a height of 10-12 metres above the earth’s surface between the periods
     * of observations (meters per second)
     */
    private String ff3;
    /**
     * Total cloud cover
     */
    private String n;
    /**
     * Present weather reported from a weather station
     */
    private String ww;
    /**
     * Past weather (weather between the periods of observation) 1
     */
    private String w1;
    /**
     * Past weather (weather between the periods of observation) 2
     */
    private String w2;
    /**
     * Minimum air temperature (degrees Celsius) during the past period (not exceeding 12 hours)
     */
    private Float tn;
    /**
     * Maximum air temperature (degrees Celsius) during the past period (not exceeding 12 hours)
     */
    private Float tx;
    /**
     * Clouds of the genera Stratocumulus, Stratus, Cumulus and Cumulonimbus
     */
    private String cl;
    /**
     * Amount of all the CL cloud present or, if no CL cloud is present, the amount of all the CM cloud present
     */
    private String nh;
    /**
     * Height of the base of the lowest clouds (m)
     */
    private String h;
    /**
     * Clouds of the genera Altocumulus, Altostratus and Nimbostratus
     */
    private String cm;
    /**
     * Clouds of the genera Cirrus, Cirrocumulus and Cirrostratus
     */
    private String ch;
    /**
     * Horizontal visibility (km)
     */
    private Float vv;
    /**
     * Dew point temperature at a height of 2 metres above the earth's surface (degrees Celsius)
     */
    private Float td;
    /**
     * Amount of precipitation (millimeters)
     */
    private Float rrr;
    /**
     * The period of time during which the specified amount of precipitation was accumulated
     */
    private Float tr;
    /**
     * State of the ground without snow or measurable ice cover
     */
    private String e;
    /**
     * The minimum soil surface temperature at night (degrees Celsius)
     */
    private Float tg;
    /**
     * State of the ground with snow or measurable ice cover.
     */
    private String eApostrophe;
    /**
     * Snow depth (cm)
     */
    private Float sss;
}
