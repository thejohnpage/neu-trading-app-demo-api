package com.neueda.leap.trading.client;
import java.util.UUID;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;
/** Resolves the client identity established by NestJS token validation. */
@Component @RequestScope
@ConditionalOnProperty(name="app.auth.mode",havingValue="jwt")
public class JwtCurrentClient implements CurrentClient {
 private final HttpServletRequest request; public JwtCurrentClient(HttpServletRequest request){this.request=request;}
 @Override public UUID clientId(){Object type=request.getAttribute("authSubjectType"),sub=request.getAttribute("authSubjectId");if(!"CLIENT".equals(type)||sub==null)throw new IllegalStateException("Authenticated CLIENT required");return UUID.fromString(sub.toString());}
}
