package com.apigateways.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Collections;

@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        
        // 1. Allow your Frontend URL
        corsConfig.setAllowedOrigins(Collections.singletonList("http://localhost:4200"));
        
        // 2. Allow all HTTP methods (GET, POST, PUT, DELETE, OPTIONS)
        corsConfig.setMaxAge(3600L);
        corsConfig.addAllowedMethod("*");
        
        // 3. Allow all headers
        corsConfig.addAllowedHeader("*");
        
        // 4. Allow Credentials (for cookies/tokens)
        corsConfig.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }
}