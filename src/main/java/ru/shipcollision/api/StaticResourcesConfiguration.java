package ru.shipcollision.api;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.GzipResourceResolver;

import java.nio.file.Paths;

/**
 * Отдает аватарки как статику.
 */
@Configuration
@ConditionalOnWebApplication
@EnableWebMvc
public class StaticResourcesConfiguration implements WebMvcConfigurer {

    public static final int CACHE_PERIOD = 3600;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        /*
         * Если убрать / в конце, то все поломается. При этом toAbsolutePath по умолчанию убирает слеш!
         * Вы будете писать GET http://localhost:8080/uploads/2018/3/8/file.png,
         * а он будет маппить в uploads(нет слеша)2018/3/8/file.png и естественно ничего не найдет!
         */
        final String resourseLocation = "file://" + Paths.get("uploads").toAbsolutePath().toString() + '/';
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(resourseLocation)
                .setCachePeriod(CACHE_PERIOD)
                .resourceChain(true)
                .addResolver(new GzipResourceResolver());
    }
}
