package com.fms.demoFMS.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.beans.factory.annotation.Value;
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
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   CorsConfigurationSource corsConfigurationSource) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disable CSRF for REST APIs
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .authorizeHttpRequests(auth -> auth
                        // 1. Static Web Resources & UI Pages
                        .requestMatchers(
                                "/",
                                "/index.html",
                                "/styles.css",
                                "/script.js",
                                "/favicon.ico",
                                "/Login/**",
                                "/dashboard.html",
                                "/principal-dashboard.html",
                                "/student-portal.html",
                                "/teacher-portal.html",
                                "/students/*.html",
                                "/students/*.css",
                                "/students/*.js",
                                "/teachers/*.html",
                                "/teachers/*.css",
                                "/teachers/*.js",
                                "/feepayment/**",
                                "/feeupdate/**"
                        ).permitAll()

                        // 2. Auth Endpoints
                        .requestMatchers("/api/auth/**").authenticated()

                        // 3. Student Self-Service (Student & Admin)
                        .requestMatchers(
                                "/student/me/**",
                                "/api/student/me/**",
                                "/paymyfee/student/pay"
                        ).hasAnyRole("STUDENT", "ADMIN")

                        // 4. Teacher Self-Service & Assigned Views
                        .requestMatchers(
                                "/teachers/my-students",
                                "/api/teacher/my-students",
                                "/teachers/me",
                                "/api/teacher/me"
                        ).hasAnyRole("TEACHER", "ADMIN")

                        // 5. Teachers can view all students and update student fees
                        .requestMatchers(HttpMethod.GET, "/student/all", "/api/student/all").hasAnyRole("TEACHER", "ADMIN")
                        .requestMatchers("/teacherupdate/**").hasAnyRole("TEACHER", "ADMIN")
                        .requestMatchers("/paymyfee/**").hasAnyRole("TEACHER", "ADMIN")

                        // 6. Admin-Only Management Endpoints
                        .requestMatchers(
                                "/student/addstudent",
                                "/student/updatestudent/**",
                                "/student/deletestudent/**",
                                "/teachers/addteacher",
                                "/teachers/update/**",
                                "/teachers/deleteteacher/**",
                                "/api/dashboard/**",
                                "/api/principal/**"
                        ).hasRole("ADMIN")

                        // 7. General Student/Teacher management endpoints (Teacher & Admin)
                        .requestMatchers("/student/**", "/api/student/**").hasAnyRole("TEACHER", "ADMIN")
                        .requestMatchers("/teachers/**", "/api/teacher/**").hasAnyRole("TEACHER", "ADMIN")

                        // 8. Any other request requires authentication
                        .anyRequest().authenticated()
                )
                // Exception handling without browser auth dialogs
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"status\":401,\"error\":\"Unauthorized\",\"message\":\"Invalid credentials or session expired.\"}");
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"status\":403,\"error\":\"Forbidden\",\"message\":\"Access denied: insufficient permissions for this resource.\"}");
                        })
                )
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
    public CorsConfigurationSource corsConfigurationSource(
            @Value("${app.cors.allowed-origin}") String frontendOrigin) {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(frontendOrigin));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

