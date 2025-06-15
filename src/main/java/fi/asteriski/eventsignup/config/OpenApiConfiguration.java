package fi.asteriski.eventsignup.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Event Signup API").version("1.0.0"))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes(
                                "bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT"))
                        // Define only the responses you want to expose
                        .addResponses("200", new ApiResponse().description("OK"))
                        .addResponses("400", new ApiResponse().description("Bad Request"))
                        .addResponses("401", new ApiResponse().description("Unauthorized"))
                        .addResponses("404", new ApiResponse().description("Not Found"))
                        .addResponses("406", new ApiResponse().description("Not Acceptable"))
                        .addResponses("500", new ApiResponse().description("Internal Server Error"))
                        .addResponses("507", new ApiResponse().description("Insufficient Storage")));
    }
}
