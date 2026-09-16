package com.neueda.leap.trading.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Authentication is owned by the separate NestJS auth API. Until JWT
        // validation is integrated, demo mode permits the current-client routes
        // and CurrentClient supplies Joanna's fixture identity. No credentials
        // are authenticated by this Spring application in demo mode.
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/actuator/health",
                                "/api/v1/version",
                                "/api/v1/instruments/**",
                                "/api/v1/me/**")
                        .permitAll()
                        .anyRequest().denyAll())
                .build();
    }
}
