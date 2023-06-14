package org.clematis.weather.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import jworkspace.weather.Observation;
import jworkspace.weather.ObservationKey;

/**
 * @author Anton Troshin
 */
@RepositoryRestResource(path = "observations")
public interface ObservationRepository extends PagingAndSortingRepository<Observation, ObservationKey> {
}
