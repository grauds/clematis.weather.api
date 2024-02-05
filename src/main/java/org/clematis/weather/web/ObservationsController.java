package org.clematis.weather.web;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.clematis.weather.repository.ObservationRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jworkspace.weather.model.dto.ObservationDTO;
import lombok.extern.java.Log;

/**
 * @author Anton Troshin
 */
@RestController
@Log
public class ObservationsController {

    @Autowired
    private ObservationRepository observationRepository;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping(value = "/observations/byDay")
    List<ObservationDTO> getDayObservations(@RequestParam("day")
                                            @DateTimeFormat(pattern = "yyyy-MM-dd")
                                            Date date) {
        return observationRepository.getDayObservations(date).stream()
           .map(observation -> modelMapper.map(observation, ObservationDTO.class))
           .collect(Collectors.toList());
    }
}
