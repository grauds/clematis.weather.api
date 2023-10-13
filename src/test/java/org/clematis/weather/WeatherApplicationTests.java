package org.clematis.weather;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@SpringBootTest(classes = WeatherApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class WeatherApplicationTests {

    public static final Logger LOGGER = LoggerFactory.getLogger(WeatherApplication.class);

    private static final DockerImageName IMAGE = DockerImageName.parse(MySQLContainer.NAME);

    private final static MySQLContainer<?> container;

    static {
        container = new MySQLContainer<>(IMAGE)
                .withUsername("weather")
                .withPassword("password")
                .withLogConsumer(new Slf4jLogConsumer(LOGGER));
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
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT version()")) {
            Assertions.assertTrue(rs.next(), "has row");
        }
    }
}
