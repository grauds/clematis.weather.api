package org.clematis.weather.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import jworkspace.weather.model.WeatherImage;

/**
 * @author Anton Troshin
 */
@RepositoryRestResource(path = "images")
public interface ImagesRepository extends PagingAndSortingRepository<WeatherImage, Integer> {
}
