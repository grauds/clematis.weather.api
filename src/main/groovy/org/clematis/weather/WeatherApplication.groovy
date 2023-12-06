package org.clematis.weather

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan

/**
 * @author Anton Troshin
 */
@SpringBootApplication
@EntityScan(basePackages = ["jworkspace", "org.clematis.weather"])
@SuppressWarnings(["PMD", "checkstyle:hideutilityclassconstructor"])
@SuppressFBWarnings("EI_EXPOSE_REP")
class WeatherApplication {

    static void main(String[] args) {
        SpringApplication.run(WeatherApplication.class, args)
    }
}
