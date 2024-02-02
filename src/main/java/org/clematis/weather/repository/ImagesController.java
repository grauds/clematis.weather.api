package org.clematis.weather.repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Anton Troshin
 */
@Component
public class ImagesController {
    
    private String path = "/home/clematis/weather/images";

    @Autowired
    private ImagesRepository imagesRepository;

    @GetMapping(
        value = "/image",
        produces = MediaType.IMAGE_JPEG_VALUE
    )
    public @ResponseBody byte[] getImageWithMediaType(@RequestParam("date") @DateTimeFormat(pattern="yyyy-MM-dd")
                                                      Date date) throws IOException {
        List<String> filesNames = imagesRepository.getImages(date);
        return !filesNames.isEmpty() ? Files.readAllBytes(Paths.get(path, filesNames.get(0))) : new byte[0];
    }
}
