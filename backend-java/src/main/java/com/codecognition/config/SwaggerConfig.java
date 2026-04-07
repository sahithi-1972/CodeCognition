package com.codecognition.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("CodeCognition AI - Repository Analysis API")
                        .version("3.3.0")
                        .description("Professional repository intelligence for secure engineering teams. " +
                                "Detect vulnerabilities, analyze code quality, and manage multi-repository insights.")
                        .contact(new Contact()
                                .name("CodeCognition Team")
                                .url("https://codecognition.ai")
                                .email("support@codecognition.ai")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT authentication token. Obtain from /auth/login endpoint.")));
    }
}
