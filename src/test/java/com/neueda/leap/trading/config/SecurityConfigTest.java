package com.neueda.leap.trading.config;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.mock.web.MockHttpServletRequest;
class SecurityConfigTest {
 @Test void corsAllowsConfiguredDevelopmentOriginsAndHeaders() {
  CorsConfigurationSource source=new SecurityConfig().corsConfigurationSource();
  MockHttpServletRequest req=new MockHttpServletRequest("GET","/api/v1/orders");
  CorsConfiguration cors=source.getCorsConfiguration(req);
  assertNotNull(cors);
  assertEquals(List.of("http://localhost:4200","http://localhost:4201"),cors.getAllowedOrigins());
  assertTrue(cors.getAllowedMethods().contains("OPTIONS"));
  assertTrue(cors.getAllowedHeaders().contains("Authorization"));
  assertEquals(Boolean.TRUE,cors.getAllowCredentials());
  assertEquals(3600L,cors.getMaxAge());
 }
 @Test void corsDoesNotApplyOutsideApiPaths() {
  CorsConfigurationSource source=new SecurityConfig().corsConfigurationSource();
  assertNull(source.getCorsConfiguration(new MockHttpServletRequest("GET","/not-api")));
 }
}
