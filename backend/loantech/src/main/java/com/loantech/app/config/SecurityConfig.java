// SecurityConfig.java - CORS CORRETTO
package com.loantech.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Disabilita CSRF per API REST
                .csrf(csrf -> csrf.disable())

                // Configura CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Configura sessioni stateless
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Permetti TUTTE le richieste
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())

                // Disabilita frame options per H2 Console
                .headers(headers -> headers.frameOptions().disable());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // USA allowedOriginPatterns invece di allowedOrigins
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));

        // Metodi HTTP permessi
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH"));

        // Header permessi
        configuration.setAllowedHeaders(Arrays.asList("*"));

        // IMPORTANTE: Disabilita credentials per evitare conflitto
        configuration.setAllowCredentials(false);

        // Applica a tutti i path
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}