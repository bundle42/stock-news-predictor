package com.example.board.board.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((req, res, e) -> {
                            String uri = req.getRequestURI();

                            if (uri.startsWith("/api")) {
                                res.setStatus(401);
                            } else {
                                res.sendRedirect("/member/login");
                            }
                        })
                        .accessDeniedHandler((req, res, e) -> {
                            String uri = req.getRequestURI();

                            if (uri.startsWith("/api")) {
                                res.setStatus(403);
                            } else {
                                res.sendError(403);
                            }
                        })
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/member/**").permitAll()

                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        .requestMatchers("/api/**", "/board/**").authenticated()

                        .anyRequest().permitAll()
                )
                .formLogin(form -> form
                        .loginProcessingUrl("/member/login")
                        .successHandler((req, res, auth) -> {
                            res.setStatus(200);
                        })
                        .failureHandler((req, res, e) -> {
                            res.setStatus(401);
                        })
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/member/logout")
                        .logoutSuccessHandler((req, res, auth) -> {
                            res.setStatus(200);
                        })
                )
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of("http://localhost:5173"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}