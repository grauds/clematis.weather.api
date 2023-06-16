package jworkspace.weather.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

import jworkspace.weather.model.WeatherImage;
import lombok.extern.java.Log;
/**
 * @author Anton Troshin
 */
@Log
public class WeatherImageFactory {

    public static final String TAG_DATE_TIME = "Date/Time";

    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");

    private WeatherImageFactory() {}

    public static WeatherImage create(Path imageFile) {

        if (imageFile != null && Files.exists(imageFile) && !Files.isDirectory(imageFile)) {
            try {
                Metadata metadata = ImageMetadataReader.readMetadata(Files.newInputStream(imageFile));

                WeatherImage weatherImage = new WeatherImage();
                // [Exif IFD0] Date/Time - 2022:07:11 09:58:21
                for (Directory directory : metadata.getDirectories()) {
                    for (Tag tag : directory.getTags()) {
                        if (tag.getTagName().equals(TAG_DATE_TIME)) {
                            weatherImage.getKey().setDate(
                                FORMATTER.parse(tag.getDescription())
                            );
                        }
                    }
                }
                if (weatherImage.getKey().getDate() == null) {
                    return null;
                }

                weatherImage.getKey().setPath(imageFile.toAbsolutePath().toString());
                return weatherImage;

            } catch (ImageProcessingException | IOException | ParseException e) {
                log.log(Level.SEVERE, e.getMessage());
            }
        }
        return null;
    }
}
