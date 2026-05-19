package com.runalb.ondemand_service.config;

import com.runalb.ondemand_service.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) {
        return http
                .cors(cors -> {})
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // OpenAPI / Swagger UI
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html")
                        .permitAll()

                        // Webhook
                        .requestMatchers("/webhook/**").permitAll()

                        // User
                        .requestMatchers(HttpMethod.POST, "/api/v1/users").permitAll()

                        // Auth
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/**").permitAll()
                        
                        // Catalog categories
                        .requestMatchers(HttpMethod.POST, "/api/v1/catalog/categories/**").hasRole("SUPER_ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/catalog/categories/**").hasRole("SUPER_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/catalog/categories/**").hasRole("SUPER_ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/catalog/categories/*/services/**").hasAnyRole("SUPER_ADMIN")

                        // Catalog services
                        .requestMatchers(HttpMethod.POST, "/api/v1/catalog/services/**").hasRole("SUPER_ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/catalog/services/**").hasRole("SUPER_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/catalog/services/**").hasRole("SUPER_ADMIN")

                        // Catalog service offerings
                        .requestMatchers(HttpMethod.GET, "/api/v1/catalog/services/*/business-offerings").authenticated()

                        // Business 
                        .requestMatchers(HttpMethod.POST, "/api/v1/businesses/**").hasRole("PROVIDER")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/businesses/**").hasRole("PROVIDER")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/businesses/**").hasRole("PROVIDER")
                        
                        // Business offerings
                        .requestMatchers(HttpMethod.POST, "/api/v1/businesses/*/offerings/**").hasRole("PROVIDER")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/businesses/*/offerings/**").hasRole("PROVIDER")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/businesses/*/offerings/**").hasRole("PROVIDER")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/businesses/*/offerings/*/verify").hasRole("SUPER_ADMIN")

                        // All other routes
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().permitAll())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
