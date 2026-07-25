package jworkspace.weather.model;

import java.util.Date;

public interface ObservationProjection {
    Integer getWeatherStationId();
    Date getDate();
    Float getDistance();
}
