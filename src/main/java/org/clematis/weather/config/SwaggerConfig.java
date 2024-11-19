package org.clematis.weather.config;
/*
import static org.springdoc.core.Constants.ALL_PATTERN;
import org.springdoc.core.GroupedOpenApi;
import org.springdoc.core.SwaggerUiConfigProperties;
import org.springdoc.core.customizers.OpenApiCustomiser;*/
//import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Configuration;
/*
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
*/
/**
 *
 * @author Anton Troshin
 */
@Configuration
public class SwaggerConfig {

    private final BuildProperties buildProperties;


    public SwaggerConfig(BuildProperties buildProperties) {
        this.buildProperties = buildProperties;
    }
/*

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().info(getInfo());
    }

    public Info getInfo() {
        return new Info().title("Weather API")
                .summary("Hateoas Restful API for a local weather archive")
                .description("")
                .version(buildProperties.getVersion())
                .description(buildProperties.getName());
    }*/
/*
    @Bean
    public GroupedOpenApi actuatorApi(OpenApiCustomiser actuatorOpenApiCustomiser,
                                      WebEndpointProperties endpointProperties) {
        return GroupedOpenApi.builder()
                .group("Actuator")
                .pathsToMatch(endpointProperties.getBasePath() + ALL_PATTERN)
                .addOpenApiCustomiser(actuatorOpenApiCustomiser)
                .addOpenApiCustomiser(openApi -> openApi.info(new Info()
                        .title("Money Tracker Actuator API")
                        .version(buildProperties.getVersion()))
                )
                .pathsToExclude("/rest/actuator/health/**")
                .pathsToExclude("/rest/actuator/health/*")
                .build();
    }

    @Bean
    public GroupedOpenApi observationsApi() {
        return GroupedOpenApi.builder()
                .group("Observations")
                .pathsToMatch("/api/observations")
                .build();
    }

    @Bean
    public GroupedOpenApi imagesApi() {
        return GroupedOpenApi.builder()
            .group("Images")
            .pathsToMatch("/api/images")
            .build();
    }

    @Bean
    SwaggerUiConfigProperties swaggerUiConfig() {
        SwaggerUiConfigProperties config = new SwaggerUiConfigProperties();
        config.setShowCommonExtensions(true);
        return config;
    }
*/
}
