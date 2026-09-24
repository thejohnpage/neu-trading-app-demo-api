package com.neueda.leap.trading.config;
import java.util.List;
import org.springframework.context.annotation.*;import org.springframework.security.config.annotation.web.builders.HttpSecurity;import org.springframework.security.config.http.SessionCreationPolicy;import org.springframework.security.web.*;import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;import org.springframework.web.cors.*;
@Configuration public class SecurityConfig {
 @Bean SecurityFilterChain securityFilterChain(HttpSecurity http,AuthValidationFilter authFilter)throws Exception{
  return http.csrf(c->c.disable()).cors(c->{}).formLogin(c->c.disable()).httpBasic(c->c.disable()).logout(c->c.disable()).requestCache(c->c.disable())
   .sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
   .authorizeHttpRequests(a->a
    .requestMatchers("/api/v1/version","/v3/api-docs/**","/swagger-ui/**","/swagger-ui.html","/actuator/health","/actuator/info","/api/v1/registration/client").permitAll()
    .requestMatchers("/api/v1/admin/**").hasAuthority("TYPE_ADMIN")
    .requestMatchers("/api/v1/me/**","/api/v1/orders/**").hasAuthority("TYPE_CLIENT")
    .requestMatchers("/api/v1/instruments/**").authenticated().anyRequest().authenticated())
   .addFilterBefore(authFilter,UsernamePasswordAuthenticationFilter.class).build();
 }
 @Bean CorsConfigurationSource corsConfigurationSource(){CorsConfiguration c=new CorsConfiguration();c.setAllowedOrigins(List.of("http://localhost:4200","http://localhost:4201"));c.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));c.setAllowedHeaders(List.of("Authorization","Content-Type","Accept"));c.setExposedHeaders(List.of("Location"));c.setAllowCredentials(true);c.setMaxAge(3600L);UrlBasedCorsConfigurationSource s=new UrlBasedCorsConfigurationSource();s.registerCorsConfiguration("/api/**",c);return s;}
}
