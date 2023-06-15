package jworkspace.weather.parser

import jworkspace.weather.model.Observation
import jworkspace.weather.service.WeatherImporter
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.hibernate.cfg.Configuration
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * @author Anton Troshin
 */
class WeatherImporterTest {

    private static final int TEST_DATA_SIZE = 63237;

    private SessionFactory sessionFactory;

    private Session session = null;

    @BeforeEach
    void before() {
        // set up the session factory
        Configuration configuration = new Configuration();
        configuration.addAnnotatedClass(Observation.class);
        configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        configuration.setProperty("hibernate.connection.driver_class", "org.h2.Driver");
        configuration.setProperty("hibernate.connection.url", "jdbc:h2:./src/test/resources/db/mem");
        configuration.setProperty("hibernate.hbm2ddl.auto", "create");
        sessionFactory = configuration.buildSessionFactory();
        session = sessionFactory.openSession();
    }

    @Test
    void test() {
        int imported = WeatherImporter.loadWeatherData(this.session);
        Assertions.assertEquals(TEST_DATA_SIZE, imported);
    }

    @AfterEach
    void after() {
        session.close();
        sessionFactory.close();
    }
}
