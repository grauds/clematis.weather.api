package org.clematis.weather.web;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.clematis.weather.repository.ImagesRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import jworkspace.weather.model.WeatherImage;
import jworkspace.weather.model.dto.WeatherImageDTO;
import lombok.extern.java.Log;

@RestController
@Log
public class ImagesController {

    @Value("${jworkspace.weather.images.dir}")
    private String path;

    @Autowired
    private ImagesRepository imagesRepository;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Get a random image for a specific day with a fallback to the same day/month in other years.
     * URL: /image/random?date=2025-06-30
     */
    @GetMapping(value = "/image/random", produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    public byte[] getRandomDayImageWithFallback(
        @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {

        Optional<WeatherImage> optionalImage = imagesRepository.getRandomDayImageWithFallback(date);
        return returnImageBytesOrEmpty(optionalImage);
    }

    /**
     * Get a random image within a specific month.
     * URL: /image/random/month?date=2025-06-01
     */
    @GetMapping(value = "/image/random/month", produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    public byte[] getRandomMonthImage(
        @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {

        Optional<WeatherImage> optionalImage = imagesRepository.getRandomImageByMonth(date);
        return returnImageBytesOrEmpty(optionalImage);
    }

    /**
     * Get a random image within a specific year.
     * URL: /image/random/year?date=2025-01-01
     */
    @GetMapping(value = "/image/random/year", produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    public byte[] getRandomYearImage(
        @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date
    ) {

        Optional<WeatherImage> optionalImage = imagesRepository.getRandomImageByYear(date);
        return returnImageBytesOrEmpty(optionalImage);
    }

    /**
     * Existing legacy single image endpoint (kept for backwards compatibility)
     */
    @SuppressWarnings("checkstyle:ReturnCount")
    @GetMapping(value = "/image", produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    public byte[] getImageWithMediaType(
        @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date
    ) {
        List<WeatherImage> filesNames = imagesRepository.getDayImages(date);
        if (filesNames.isEmpty()) {
            return new byte[0];
        }
        try {
            return Files.readAllBytes(Paths.get(path, filesNames.getFirst().getPath()));
        } catch (IOException e) {
            log.log(Level.WARNING, e.getMessage());
            return new byte[0];
        }
    }

    @GetMapping(value = "/images/byDay")
    public List<WeatherImageDTO> getDayImages(
        @RequestParam("day") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date
    ) {
        return imagesRepository.getDayImages(date).stream()
            .map(weatherImage -> modelMapper.map(weatherImage, WeatherImageDTO.class))
            .collect(Collectors.toList());
    }

    @GetMapping(value = "/images/byMonth")
    public List<WeatherImageDTO> getMonthImages(
        @RequestParam("month") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date
    ) {
        return imagesRepository.getMonthImages(date).stream()
            .map(weatherImage -> modelMapper.map(weatherImage, WeatherImageDTO.class))
            .collect(Collectors.toList());
    }

    @GetMapping(value = "/images/byYear")
    public List<WeatherImageDTO> getYearImages(
        @RequestParam("year") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date
    ) {
        return imagesRepository.getYearImages(date).stream()
            .map(weatherImage -> modelMapper.map(weatherImage, WeatherImageDTO.class))
            .collect(Collectors.toList());
    }

    /**
     * Helper method to reduce duplicate I/O logic code
     */
    private byte[] returnImageBytesOrEmpty(Optional<WeatherImage> optionalImage) {
        return optionalImage.map(image -> {
            try {
                return Files.readAllBytes(Paths.get(path, image.getPath()));
            } catch (IOException e) {
                log.log(Level.WARNING, "Failed to read file from disk: " + e.getMessage());
                return new byte[0];
            }
        }).orElse(new byte[0]);
    }
}
