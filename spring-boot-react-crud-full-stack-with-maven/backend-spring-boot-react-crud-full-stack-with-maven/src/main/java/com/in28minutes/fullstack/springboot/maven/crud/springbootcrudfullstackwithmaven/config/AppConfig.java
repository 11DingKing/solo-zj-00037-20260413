package com.in28minutes.fullstack.springboot.maven.crud.springbootcrudfullstackwithmaven.config;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class AppConfig {

    @Value("${cors.allowed-origins:http://localhost:3000,http://localhost:4200}")
    private String[] allowedOrigins;

    private static final Set<String> ALLOWED_SORT_FIELDS = new HashSet<>(
            Arrays.asList("id", "username", "description"));

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList(allowedOrigins));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }

    public static void validateSortFields(Sort sort) {
        if (sort == null || !sort.isSorted()) {
            return;
        }
        
        for (Sort.Order order : sort) {
            String property = order.getProperty();
            if (!ALLOWED_SORT_FIELDS.contains(property)) {
                throw new IllegalArgumentException(
                    "Invalid sort field: '" + property + "'. Allowed fields: " + ALLOWED_SORT_FIELDS);
            }
        }
    }

    public static Set<String> getAllowedSortFields() {
        return ALLOWED_SORT_FIELDS;
    }
}
