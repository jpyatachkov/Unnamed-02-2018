package ru.shipcollision.api;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@ConditionalOnWebApplication
public class CorsConfiguration implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowCredentials(true)
                .allowedMethods("GET", "HEAD", "POST", "PUT", "DELETE", "PATCH")
                .allowedOrigins(
                        "http://shipcollision.me",
                        "https://shipcollision.me",
                        "http://shipcollision.herokuapp.com",
                        "https://shipcollision.herokuapp.com",
                        "http://dev-shipcollision.herokuapp.com",
                        "https://dev-shipcollision.herokuapp.com",
                        "http://localhost:5000"
                );
    }
}
