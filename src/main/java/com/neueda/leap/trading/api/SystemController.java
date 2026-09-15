package com.neueda.leap.trading.api;

import java.time.Instant;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class SystemController {

    private final String applicationName;
    private final String applicationVersion;

    public SystemController(
            @Value("${spring.application.name}") String applicationName,
            @Value("${info.app.version:unknown}") String applicationVersion) {
        this.applicationName = applicationName;
        this.applicationVersion = applicationVersion;
    }

    @GetMapping("/version")
    public Map<String, Object> version() {
        return Map.of(
                "application", applicationName,
                "version", applicationVersion,
                "timestamp", Instant.now().toString());
    }
}
