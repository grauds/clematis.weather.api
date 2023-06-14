package jworkspace.exif;

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

import lombok.extern.java.Log;
/**
 * @author Anton Troshin
 */
@Log
public class WeatherImageFactory {

    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");

    public static WeatherImage create(Path imageFile) {
        if (imageFile != null && Files.exists(imageFile)) {
            try {
                Metadata metadata = ImageMetadataReader.readMetadata(Files.newInputStream(imageFile));

                WeatherImage weatherImage = new WeatherImage();

                // [Exif IFD0] Date/Time - 2022:07:11 09:58:21
                for (Directory directory : metadata.getDirectories()) {
                    for (Tag tag : directory.getTags()) {
                        if (tag.getTagName().equals("Date/Time")) {
                            weatherImage.getKey().setDate(
                                formatter.parse(tag.getDescription())
                            );
                        }
                    }
                }
                weatherImage.getKey().setPath(imageFile.toString());

                return weatherImage;

            } catch (ImageProcessingException | IOException | ParseException e) {
                log.log(Level.SEVERE, e.getMessage());
            }
        }
        return null;
    }
}
