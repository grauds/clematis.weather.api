package org.clematis.weather.config

import javax.persistence.EntityManager

import jworkspace.weather.Observation
import jworkspace.weather.WeatherParser

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings
import org.hibernate.Session
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.transaction.TransactionStatus
import org.springframework.transaction.support.TransactionCallbackWithoutResult
import org.springframework.transaction.support.TransactionTemplate

/**
 * @author Anton Troshin
 */
@Component
@SuppressFBWarnings
class DataImporterConfig {

    @Bean
    CommandLineRunner run(EntityManager entityManager, TransactionTemplate transactionTemplate) {
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
            result.addAll(new WeatherParser(true, "27612.01.01.2023.11.06.2023.1.0.0.en.unic.00000000.csv").read())

            for (Observation observation : result) {
                Observation existing = session.find(Observation.class, observation.getKey())
                if (existing == null) {
                    session.saveOrUpdate(observation)
                }
            }

            return result.size()
        }
        return 0
    }
}
