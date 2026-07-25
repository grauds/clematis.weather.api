package jworkspace.weather.model.dto;

import java.time.LocalDateTime;
import java.util.List;

public record WeatherMatchDTO(
    LocalDateTime matchedDate,
    Float distance,
    List<String> imagePaths
) {}