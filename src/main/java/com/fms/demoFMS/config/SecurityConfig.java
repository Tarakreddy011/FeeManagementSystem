package com.fms.demoFMS.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disable CSRF for REST APIs
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        // 1. Static HTML, CSS, JS, and UI pages are publicly accessible
                        .requestMatchers(
                                "/",
                                "/index.html",
                                "/styles.css",
                                "/script.js",
                                "/favicon.ico",
                                "/Login/**",
                                "/dashboard.html",
                                "/students/**",
                                "/teachers/**",
                                "/feepayment/**",
                                "/feeupdate/**"
                        ).permitAll()

                        // 2. Auth verification endpoints require valid credentials
                        .requestMatchers("/api/auth/**").authenticated()

                        // 3. API endpoints are secured with Spring Security
                        .requestMatchers("/student/**").authenticated()
                        .requestMatchers("/teachers/**").authenticated()
                        .requestMatchers("/paymyfee/**").authenticated()
                        .requestMatchers("/teacherupdate/**").authenticated()

                        // 4. Any other request requires authentication
                        .anyRequest().authenticated()
                )
                // Custom entry point suppresses WWW-Authenticate header to prevent browser's native popup
                .httpBasic(basic -> basic.authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"status\":401,\"error\":\"Unauthorized\",\"message\":\"Invalid credentials. Please try again.\"}");
                }));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new PasswordEncoder() {
            @Override
            public String encode(CharSequence rawPassword) {
                return rawPassword == null ? "" : rawPassword.toString();
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                if (rawPassword == null || encodedPassword == null) {
                    return false;
                }
                String raw = rawPassword.toString();
                if (raw.equals(encodedPassword)) {
                    return true;
                }
                // Strip '-' and any non-alphanumeric characters (for DOB passwords)
                String cleanRaw = raw.replaceAll("[^0-9a-zA-Z]", "");
                String cleanEncoded = encodedPassword.replaceAll("[^0-9a-zA-Z]", "");
                return !cleanRaw.isEmpty() && cleanRaw.equals(cleanEncoded);
            }
        };
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
