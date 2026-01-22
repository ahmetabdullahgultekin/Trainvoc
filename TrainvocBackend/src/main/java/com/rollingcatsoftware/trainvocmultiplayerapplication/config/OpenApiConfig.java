package com.rollingcatsoftware.trainvocmultiplayerapplication.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI/Swagger configuration for API documentation.
 * Access documentation at /swagger-ui.html or /v3/api-docs
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI trainvocOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Development Server"),
                        new Server()
                                .url("https://api.trainvoc.rollingcatsoftware.com")
                                .description("Production Server")
                ))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT Authentication token")
                        )
                )
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }

    private Info apiInfo() {
        return new Info()
                .title("Trainvoc Multiplayer API")
                .description("""
                        REST API for Trainvoc multiplayer vocabulary game platform.

                        ## Features
                        - **Game Rooms**: Create, join, and manage multiplayer game rooms
                        - **Quiz**: Generate and play vocabulary quizzes
                        - **Leaderboard**: Track player rankings and scores
                        - **Words**: Access vocabulary database

                        ## Authentication
                        Most endpoints require JWT authentication. Get a token by joining a room.

                        ## WebSocket
                        Real-time game updates via WebSocket at `ws://host/ws/game/{roomCode}`
                        """)
                .version("1.0.0")
                .contact(new Contact()
                        .name("Rolling Cat Software")
                        .email("contact@rollingcatsoftware.com")
                        .url("https://rollingcatsoftware.com")
                )
                .license(new License()
                        .name("MIT License")
                        .url("https://opensource.org/licenses/MIT")
                );
    }
}
