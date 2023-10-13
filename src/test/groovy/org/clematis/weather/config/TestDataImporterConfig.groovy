package org.clematis.weather.config


import javax.persistence.EntityManager
import java.nio.file.Path

import org.hibernate.Session
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import org.springframework.transaction.TransactionStatus
import org.springframework.transaction.support.TransactionCallbackWithoutResult
import org.springframework.transaction.support.TransactionTemplate

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings
import jworkspace.weather.model.Observation
import jworkspace.weather.parser.WeatherParser
import jworkspace.weather.service.WeatherImagesImporter

/**
 * @author Anton Troshin
 */
@Component
@SuppressFBWarnings
@Profile(value = "test")
class TestDataImporterConfig {

    @Value('${jworkspace.weather.images.dir}')
    private String path

    @Bean
    CommandLineRunner importWeather(EntityManager entityManager, TransactionTemplate transactionTemplate) {
        return (args) -> transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                Session session = entityManager.unwrap(Session.class)
                List<Observation> result = new WeatherParser(true,
                        "27612.01.02.2005.01.02.2006.1.0.0.en.unic.00000000.csv").read()
                for (Observation observation : result) {
                    Observation existing = session.find(Observation.class, observation.getId())
                    if (existing == null) {
                        session.saveOrUpdate(observation)
                    }
                }
            }
        })
    }

    @Bean
    CommandLineRunner importWeatherImages(EntityManager entityManager, TransactionTemplate transactionTemplate) {
        return (args) -> transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                Session session = entityManager.unwrap(Session.class)
                WeatherImagesImporter.loadWeatherImages(Path.of(path), session)
            }
        })
    }
}
