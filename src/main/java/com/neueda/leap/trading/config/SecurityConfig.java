package com.neueda.leap.trading.config;

import java.nio.charset.StandardCharsets;
import java.util.*;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.*;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.*;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Configuration
public class SecurityConfig {
 @Bean SecurityFilterChain securityFilterChain(HttpSecurity http,Converter<Jwt,? extends AbstractAuthenticationToken> jwtAuth)throws Exception{
  return http.csrf(c->c.disable()).cors(c->{}).formLogin(c->c.disable()).httpBasic(c->c.disable()).logout(c->c.disable()).requestCache(c->c.disable())
   .sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
   .authorizeHttpRequests(a->a
    .requestMatchers("/api/v1/version","/v3/api-docs/**","/swagger-ui/**","/swagger-ui.html","/actuator/health","/actuator/info","/api/v1/registration/client").permitAll()
    .requestMatchers("/api/v1/admin/**").hasAuthority("TYPE_ADMIN")
    .requestMatchers("/api/v1/me/**","/api/v1/orders/**").hasAuthority("TYPE_CLIENT")
    .requestMatchers("/api/v1/instruments/**").authenticated()
    .anyRequest().authenticated())
   .oauth2ResourceServer(o->o.jwt(j->j.jwtAuthenticationConverter(jwtAuth))).build();
 }
 @Bean JwtDecoder jwtDecoder(@Value("${JWT_SECRET}")String secret){
  NimbusJwtDecoder d=NimbusJwtDecoder.withSecretKey(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),"HmacSHA256")).macAlgorithm(org.springframework.security.oauth2.jose.jws.MacAlgorithm.HS256).build();
  d.setJwtValidator(JwtValidators.createDefaultWithIssuer("neu-trading-auth")); return d;
 }
 @Bean Converter<Jwt,? extends AbstractAuthenticationToken> jwtAuthenticationConverter(){
  return jwt->{List<GrantedAuthority> a=new ArrayList<>();String type=jwt.getClaimAsString("type");if(type!=null)a.add(new SimpleGrantedAuthority("TYPE_"+type));List<String> roles=jwt.getClaimAsStringList("roles");if(roles!=null)roles.forEach(r->a.add(new SimpleGrantedAuthority("ROLE_"+r)));return new JwtAuthenticationToken(jwt,a,jwt.getSubject());};
 }
 @Bean CorsConfigurationSource corsConfigurationSource(){CorsConfiguration c=new CorsConfiguration();c.setAllowedOrigins(List.of("http://localhost:4200","http://localhost:4201"));c.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));c.setAllowedHeaders(List.of("Authorization","Content-Type","Accept"));c.setExposedHeaders(List.of("Location"));c.setAllowCredentials(true);c.setMaxAge(3600L);UrlBasedCorsConfigurationSource s=new UrlBasedCorsConfigurationSource();s.registerCorsConfiguration("/api/**",c);return s;}
}
