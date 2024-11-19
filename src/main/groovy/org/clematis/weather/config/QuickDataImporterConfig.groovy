package org.clematis.weather.config

import java.nio.file.Path

import jakarta.persistence.EntityManager

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings
import jworkspace.weather.service.WeatherImagesImporter
import jworkspace.weather.service.WeatherImporter
import org.hibernate.Session
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import org.springframework.transaction.TransactionStatus
import org.springframework.transaction.support.TransactionCallbackWithoutResult
import org.springframework.transaction.support.TransactionTemplate


/**
 * @author Anton Troshin
 */
@Component
@SuppressFBWarnings
@Profile(value = "dev")
class QuickDataImporterConfig {

    @Value('${jworkspace.weather.images.dir}')
    private String path

    @Bean
    CommandLineRunner importWeather(EntityManager entityManager, TransactionTemplate transactionTemplate) {
        return (args) -> transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                Session session = entityManager.unwrap(Session.class)
                if (session.createQuery("select 1 from Observation").list().isEmpty()) {
                    WeatherImporter.loadWeatherData(session)
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
                if (session.createQuery("select 1 from WeatherImage").list().isEmpty()) {
                    WeatherImagesImporter.loadWeatherImages(Path.of(path), session)
                }
            }
        })
    }
}
