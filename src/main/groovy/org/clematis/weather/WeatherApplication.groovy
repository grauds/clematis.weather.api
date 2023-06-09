package org.clematis.weather

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan

/**
 * @author Anton Troshin
 */
@SpringBootApplication
@EntityScan(basePackages = ["jworkspace", "org.clematis.weather"])
@SuppressWarnings(["PMD", "checkstyle:hideutilityclassconstructor"])
class WeatherApplication {

    static void main(String[] args) {
        SpringApplication.run(WeatherApplication.class, args)
    }
}
