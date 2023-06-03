package org.clematis.weather;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
/**
 * @author Anton Troshin
 */
@SpringBootApplication
@EntityScan(basePackages = "jworkspace")
@SuppressWarnings({"PMD", "checkstyle:hideutilityclassconstructor"})
public class WeatherApplication {

    public static void main(String[] args) {
        SpringApplication.run(WeatherApplication.class, args);
    }
}
