package com.neueda.leap.trading.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Demo mode: NestJS will own authentication. CurrentClient supplies the
        // fixture identity until JWT validation is integrated into this resource server.
        return http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/health","/api/v1/version","/api/v1/instruments/**",
                                "/api/v1/me/**","/api/v1/orders/**").permitAll()
                        .anyRequest().denyAll()).build();
    }
}
