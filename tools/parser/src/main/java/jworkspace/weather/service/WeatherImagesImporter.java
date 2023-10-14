package jworkspace.weather.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.hibernate.Session;

import jworkspace.weather.model.WeatherImage;
import lombok.extern.java.Log;

/**
 * @author Anton Troshin
 */
@Log
public class WeatherImagesImporter {

    private WeatherImagesImporter() {
    }

    public static void loadWeatherImages(Path path, Session session) {
        if (session != null && session.isOpen()) {
            try (Stream<Path> stream = Files.list(path)) {
                stream.map(WeatherImageFactory::create)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList())
                    .forEach((weatherImage) -> {
                        WeatherImage existing = session.find(WeatherImage.class, weatherImage.getKey());
                        if (existing == null) {
                            session.merge(weatherImage);
                        }
                    });
            } catch (IOException e) {
                log.log(Level.SEVERE, e.getMessage());
            }
        }
    }
}
