package com.neueda.leap.trading.api;

import java.time.Instant;
import java.util.Map;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@Tag(name="System",description="Application metadata and health-related information")
/** Provides basic application metadata for API clients and operational checks. */
@RestController
@RequestMapping("/api/v1")
public class SystemController {
 private final String applicationName; private final String applicationVersion;
 public SystemController(@Value("${spring.application.name}") String applicationName,@Value("${info.app.version:unknown}") String applicationVersion){this.applicationName=applicationName;this.applicationVersion=applicationVersion;}
 /** Get API version. */
 @Operation(summary="Get API version",description="Returns the application name, configured version and current server timestamp.")
 @GetMapping("/version")
 public Map<String,Object> version(){return Map.of("application",applicationName,"version",applicationVersion,"timestamp",Instant.now().toString());}
}
