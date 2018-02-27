package ru.shipcollision.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfiguration implements WebMvcConfigurer {

    @Value("${ru.shipcollision.api.access-control-allow-origin}")
    private String accessControlAllowOrigin;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(accessControlAllowOrigin)
                .allowedMethods("DELETE", "PATCH", "POST", "PUT");
    }
}
