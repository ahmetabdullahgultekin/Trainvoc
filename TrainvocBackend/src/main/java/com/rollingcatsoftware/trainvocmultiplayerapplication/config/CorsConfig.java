package com.rollingcatsoftware.trainvocmultiplayerapplication.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                        "https://trainvoc.rollingcatsoftware.com",
                        "https://api.trainvoc.rollingcatsoftware.com",
                        "https://api.trainvoc.rollingcatsoftware.com:8443",
                        "http://localhost:5173" // Local development
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
