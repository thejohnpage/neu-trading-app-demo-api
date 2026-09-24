package com.neueda.leap.trading.config;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import java.util.*;
import tools.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/** Authenticates bearer tokens by delegating validation to the NestJS Auth API. */
@Component
public class AuthValidationFilter extends OncePerRequestFilter {
 private final HttpClient http=HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(2)).build();
 private final ObjectMapper json; private final String validationUrl;
 public AuthValidationFilter(ObjectMapper json,@Value("${app.auth.validation-url:http://localhost:3001/api/v1/auth/validate}")String url){this.json=json;this.validationUrl=url;}
 @Override protected void doFilterInternal(HttpServletRequest req,HttpServletResponse res,FilterChain chain)throws ServletException,IOException{
  String header=req.getHeader("Authorization");
  if(header==null||!header.startsWith("Bearer ")){chain.doFilter(req,res);return;}
  try{
   HttpRequest request=HttpRequest.newBuilder(URI.create(validationUrl)).timeout(Duration.ofSeconds(3)).header("Authorization",header).GET().build();
   HttpResponse<String> response=http.send(request,HttpResponse.BodyHandlers.ofString());
   if(response.statusCode()!=200){res.sendError(HttpServletResponse.SC_UNAUTHORIZED,"Invalid or expired access token");return;}
   @SuppressWarnings("unchecked") Map<String,Object> v=json.readValue(response.body(),Map.class);
   String sub=(String)v.get("sub"),type=(String)v.get("type");
   List<SimpleGrantedAuthority> authorities=new ArrayList<>();authorities.add(new SimpleGrantedAuthority("TYPE_"+type));
   Object roles=v.get("roles");if(roles instanceof List<?> list)for(Object role:list)authorities.add(new SimpleGrantedAuthority("ROLE_"+role));
   SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(sub,null,authorities));
   req.setAttribute("authSubjectId",sub);req.setAttribute("authSubjectType",type);req.setAttribute("authSubjectEmail",v.get("email"));
   chain.doFilter(req,res);
  }catch(InterruptedException e){Thread.currentThread().interrupt();res.sendError(503,"Authentication service unavailable");}
   catch(Exception e){res.sendError(503,"Authentication service unavailable");}
 }
}
