package com.finserv.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path mediaDir = Paths.get("media").toAbsolutePath().normalize();
        String mediaPath = mediaDir.toUri().toString();

        registry.addResourceHandler("/media/**")
                .addResourceLocations(mediaPath);
    }
}
