package org.clematis.weather.web;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

import org.clematis.weather.repository.ImagesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import jworkspace.weather.model.WeatherImage;
import lombok.extern.java.Log;

/**
 * @author Anton Troshin
 */
@RestController
@Log
public class ImagesController {

    @Value("${jworkspace.weather.images.dir}")
    private String path;

    @Autowired
    private ImagesRepository imagesRepository;

    @GetMapping(value = "/image", produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    public byte[] getImageWithMediaType(@RequestParam("date")
                                        @DateTimeFormat(pattern = "yyyy-MM-dd")
                                        Date date) {
        List<WeatherImage> filesNames = imagesRepository.getImages(date);
        try {
            return !filesNames.isEmpty()
                ? Files.readAllBytes(Paths.get(path, filesNames.get(0).getPath())) : new byte[0];
        } catch (IOException e) {
            log.log(Level.WARNING, e.getMessage());
            return new byte[0];
        }
    }


}
