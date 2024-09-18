package jworkspace.weather.parser

import jworkspace.weather.model.Observation
import jworkspace.weather.service.WeatherImporter
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.hibernate.cfg.Configuration
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

/**
 * @author Anton Troshin
 */
class WeatherImporterTest {

    private static final int TEST_DATA_SIZE = 67049

    private static SessionFactory sessionFactory

    private static Session session = null

    @BeforeAll
    static void before() {
        // set up the session factory
        Configuration configuration = new Configuration()
        configuration.addAnnotatedClass(Observation.class)
        configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect")
        configuration.setProperty("hibernate.connection.driver_class", "org.h2.Driver")
        configuration.setProperty("hibernate.connection.url", "jdbc:h2:./src/test/resources/db/mem")
        configuration.setProperty("hibernate.hbm2ddl.auto", "create")
        sessionFactory = configuration.buildSessionFactory()
        session = sessionFactory.openSession()
    }

    @Test
    void test() {
        int imported = WeatherImporter.loadWeatherData(session)
        Assertions.assertEquals(TEST_DATA_SIZE, imported)
    }

    @AfterAll
    static void after() {
        session.close()
        sessionFactory.close()
    }
}
