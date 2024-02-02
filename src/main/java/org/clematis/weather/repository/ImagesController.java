package org.clematis.weather.repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.java.Log;

/**
 * @author Anton Troshin
 */
@RestController
@Log
public class ImagesController {

    private String path = "/home/clematis/weather/images/";

    @Autowired
    private ImagesRepository imagesRepository;

    @GetMapping(value = "/image", produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    public byte[] getImageWithMediaType(@RequestParam("date")
                                        @DateTimeFormat(pattern = "yyyy-MM-dd")
                                        Date date) {
        List<String> filesNames = imagesRepository.getImages(date);
        try {
            return !filesNames.isEmpty() ? Files.readAllBytes(Paths.get(path, filesNames.get(0))) : new byte[0];
        } catch (IOException e) {
            log.log(Level.WARNING, e.getMessage());
            return new byte[0];
        }
    }
}
