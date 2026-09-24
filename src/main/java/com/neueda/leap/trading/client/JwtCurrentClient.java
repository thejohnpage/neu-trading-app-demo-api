package com.neueda.leap.trading.client;
import java.util.UUID;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
/** Resolves the authenticated client identity from a NestJS-issued access JWT. */
@Component
@ConditionalOnProperty(name="app.auth.mode",havingValue="jwt")
public class JwtCurrentClient implements CurrentClient {
 @Override public UUID clientId(){
  if(!(SecurityContextHolder.getContext().getAuthentication() instanceof JwtAuthenticationToken auth))throw new IllegalStateException("Authenticated JWT required");
  String type=auth.getToken().getClaimAsString("type");if(!"CLIENT".equals(type))throw new IllegalStateException("CLIENT token required");
  return UUID.fromString(auth.getToken().getSubject());
 }
}
