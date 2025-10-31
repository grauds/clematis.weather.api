package org.clematis.weather.config;

import static org.springdoc.core.utils.Constants.ALL_PATTERN;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

/**
 *
 * @author Anton Troshin
 */
@Configuration
@ComponentScan(
    basePackages = {"org.clematis.weather"}
)
public class OpenAPIConfig {

    private final BuildProperties buildProperties;

    public OpenAPIConfig(BuildProperties buildProperties) {
        this.buildProperties = buildProperties;
    }

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().info(getInfo());
    }

    public Info getInfo() {
        return new Info().title("Clematis Weather API")
            .summary("Hateoas Restful API for weather images")
            .description(buildProperties.getName())
            .version(buildProperties.getVersion());
    }

    @Bean
    public GroupedOpenApi observationsApi() {
        return GroupedOpenApi.builder()
            .group("Observations")
            .pathsToMatch("/api/observations/**")
            .addOpenApiCustomizer(openApi -> openApi.info(
                new Info()
                    .title("Observations API")
                    .version(buildProperties.getVersion())
            ))
            .build();
    }

    @Bean
    public GroupedOpenApi imagesApi() {
        return GroupedOpenApi.builder()
            .group("Images")
            .pathsToMatch("/api/images/**")
            .addOpenApiCustomizer(openApi -> openApi.info(
                new Info()
                    .title("Weather Images API")
                    .version(buildProperties.getVersion())
            ))
            .build();
    }

    @Bean
    public GroupedOpenApi actuatorApi(WebEndpointProperties endpointProperties) {
        return GroupedOpenApi.builder()
            .group("Actuator")
            .pathsToMatch(endpointProperties.getBasePath() + ALL_PATTERN)
            .addOpenApiCustomizer(openApi -> openApi.info(
                    new Info()
                        .title("Weather Actuator API")
                        .version(buildProperties.getVersion())
                )
            )
            .build();
    }
}
