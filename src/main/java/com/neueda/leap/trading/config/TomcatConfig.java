package com.neueda.leap.trading.config;

import org.springframework.boot.tomcat.servlet.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Preserve encoded slashes in instrument symbols such as EUR%2FUSD.
 * The connector must accept the request before Spring MVC can route it.
 */
@Configuration
public class TomcatConfig {

    @Bean
    WebServerFactoryCustomizer<TomcatServletWebServerFactory> encodedSlashCustomizer() {
        return factory -> factory.addConnectorCustomizers(
            connector -> connector.setProperty("encodedSolidusHandling", "passthrough")
        );
    }
}
