package com.niloy.scholarshelf.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger/OpenAPI configuration for the ScholarShelf API.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI scholarShelfOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ScholarShelf - Book Exchange Platform API")
                        .description("RESTful API documentation for the ScholarShelf Book Exchange Platform. " +
                                "Supports Buyer, Seller, and Admin roles with JWT-based authentication.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Niloy")
                                .email("niloy@scholarshelf.com")))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("Bearer Authentication",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .bearerFormat("JWT")
                                        .scheme("bearer")));
    }
}

