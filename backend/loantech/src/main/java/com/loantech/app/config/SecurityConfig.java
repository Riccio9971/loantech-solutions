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
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz
                        // Permetti tutte le richieste di autenticazione
                        .requestMatchers("/api/auth/**").permitAll()

                        // Permetti endpoint pubblici
                        .requestMatchers("/api/public/**").permitAll()

                        // Permetti console H2 per sviluppo
                        .requestMatchers("/h2-console/**").permitAll()

                        // Permetti endpoint di health check
                        .requestMatchers("/actuator/**").permitAll()

                        // Permetti pagine di errore
                        .requestMatchers("/error").permitAll()

                        // PER ORA: Permetti TUTTE le richieste API per il testing
                        .requestMatchers("/api/**").permitAll()

                        // Tutte le altre richieste richiedono autenticazione
                        .anyRequest().authenticated()
                )
                .headers(headers -> headers.frameOptions().disable()); // Per H2 Console

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Permetti tutte le origini per sviluppo
        configuration.setAllowedOriginPatterns(List.of("*"));

        // Permetti tutti i metodi HTTP
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        // Permetti tutti gli header
        configuration.setAllowedHeaders(List.of("*"));

        // Permetti credenziali
        configuration.setAllowCredentials(true);

        // Configura per tutti i path
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}