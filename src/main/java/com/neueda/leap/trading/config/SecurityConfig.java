package com.neueda.leap.trading.config;
import java.util.List;
import org.springframework.context.annotation.*;
import com.neueda.leap.trading.audit.AuditTrailFilter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;

/** Central RBAC policy for the trading API. */
@Configuration public class SecurityConfig {
 @Bean SecurityFilterChain securityFilterChain(HttpSecurity http,AuthValidationFilter authFilter,AuditTrailFilter auditFilter)throws Exception{
  return http.csrf(c->c.disable()).cors(c->{}).formLogin(c->c.disable()).httpBasic(c->c.disable()).logout(c->c.disable()).requestCache(c->c.disable())
   .sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
   .authorizeHttpRequests(a->a
    .requestMatchers("/api/v1/version","/v3/api-docs/**","/swagger-ui/**","/swagger-ui.html","/actuator/health","/actuator/info","/api/v1/registration/client").permitAll()
    .requestMatchers("/api/v1/admin/rbac/**").hasAuthority("CAP_ROLE_MANAGEMENT")
    .requestMatchers("/api/v1/admin/users/**").hasAuthority("CAP_USER_MANAGEMENT")
    .requestMatchers("/api/v1/admin/orders/**").hasAuthority("CAP_ORDER_OPERATIONS")
    .requestMatchers("/api/v1/admin/audit/**").hasAuthority("CAP_AUDIT_VIEW")
    .requestMatchers("/api/v1/admin/reports/**").hasAuthority("CAP_REPORTING")
    .requestMatchers("/api/v1/admin/**").hasAuthority("CAP_USER_MANAGEMENT")
    .requestMatchers("/api/v1/me/**","/api/v1/orders/**").hasAuthority("TYPE_CLIENT")
    .requestMatchers("/api/v1/instruments/**").authenticated()
    .anyRequest().authenticated())
   .addFilterBefore(authFilter,UsernamePasswordAuthenticationFilter.class)
   .addFilterAfter(auditFilter,AuthValidationFilter.class).build();
 }
 @Bean CorsConfigurationSource corsConfigurationSource(){CorsConfiguration c=new CorsConfiguration();c.setAllowedOrigins(List.of("http://localhost:4200","http://localhost:4201"));c.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));c.setAllowedHeaders(List.of("Authorization","Content-Type","Accept"));c.setExposedHeaders(List.of("Location"));c.setAllowCredentials(true);c.setMaxAge(3600L);UrlBasedCorsConfigurationSource s=new UrlBasedCorsConfigurationSource();s.registerCorsConfiguration("/api/**",c);return s;}
}
