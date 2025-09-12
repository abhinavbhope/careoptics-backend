package com.specsShope.specsBackend.Config;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final String[] allowedOrigins = {
            "http://localhost:9002",
            "https://careoptics.vercel.app",
            "careoptics-git-main-abhinavdecides-projects.vercel.app"// <-- no trailing space
    };

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    @PostConstruct
    public void printAllowedOrigins() {
        System.out.println("Allowed CORS origins configured:");
        for (String origin : allowedOrigins) {
            System.out.println(" - " + origin);
        }
    }
}
