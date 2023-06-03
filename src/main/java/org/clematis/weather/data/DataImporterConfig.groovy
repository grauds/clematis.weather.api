package org.clematis.weather.data

import javax.persistence.EntityManager

import jworkspace.weather.Observation
import jworkspace.weather.WeatherParser

import org.hibernate.Session
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.TransactionStatus
import org.springframework.transaction.support.TransactionCallbackWithoutResult
import org.springframework.transaction.support.TransactionTemplate

/**
 * @author Anton Troshin
 */
@Configuration
class DataImporterConfig {

    @Bean
    CommandLineRunner registerWeatherImporter(EntityManager entityManager, TransactionTemplate transactionTemplate) {
        return (args) -> transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                loadWeatherData(entityManager.unwrap(Session.class))
            }
        })
    }

    static int loadWeatherData(Session session) {

        if (session != null && session.isOpen()) {

            List<Observation> result = new WeatherParser(true, "27612.01.02.2005.01.02.2006.1.0.0.en.unic.00000000.csv").read()

            result.addAll(new WeatherParser(true, "27612.01.02.2006.01.02.2010.1.0.0.en.unic.00000000.csv").read())
            result.addAll(new WeatherParser(true, "27612.01.02.2010.01.02.2015.1.0.0.en.unic.00000000.csv").read())
            result.addAll(new WeatherParser(true, "27612.01.02.2015.01.02.2021.1.0.0.en.unic.00000000.csv").read())
            result.addAll(new WeatherParser(true, "27612.01.02.2021.01.02.2022.1.0.0.en.unic.00000000.csv").read())
            result.addAll(new WeatherParser(true, "27612.01.02.2022.21.12.2022.1.0.0.en.unic.00000000.csv").read())

            for (Observation observation : result) {
                session.saveOrUpdate(observation)
            }

            return result.size()
        }
        return 0
    }
}
