package org.clematis.weather;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.logging.Logger;

@Testcontainers
@SpringBootTest(classes = WeatherApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@SuppressFBWarnings("RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE")
public class WeatherApplicationTests {

    public static final Logger LOGGER = Logger.getLogger(WeatherApplication.class.getName());

    private static final DockerImageName MYSQL_80_IMAGE = DockerImageName.parse("mysql:8.0.36");

    private final static MySQLContainer<?> container;

    static {
        container = new MySQLContainer<>(MYSQL_80_IMAGE)
                .withUsername("weather")
                .withPassword("password");
        container.start();
    }

    @DynamicPropertySource
    static void init(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", container::getJdbcUrl);
        registry.add("spring.datasource.password", container::getPassword);
        registry.add("spring.datasource.username", container::getUsername);
    }

    @Test
    public void canConnectToContainer() throws Exception {
        try (Connection connection = DriverManager
                .getConnection(container.getJdbcUrl(), container.getUsername(), container.getPassword());
            Statement stmt = connection.createStatement()) {

            ResultSet rs = stmt.executeQuery("SELECT version()");
            Assertions.assertTrue(rs.next(), "has row");
        }
    }
}
