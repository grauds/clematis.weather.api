package org.clematis.weather

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings
import org.modelmapper.ModelMapper
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.persistence.autoconfigure.EntityScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@EntityScan(basePackages = ["jworkspace", "org.clematis.weather"])
@ComponentScan(["org.clematis.weather", "org.clematis.logging"])
@SuppressWarnings(["PMD", "checkstyle:hideutilityclassconstructor"])
@SuppressFBWarnings("EI_EXPOSE_REP")
class WeatherApplication {

    static void main(String[] args) {
        SpringApplication.run(WeatherApplication.class, args)
    }

    @Bean
    ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
