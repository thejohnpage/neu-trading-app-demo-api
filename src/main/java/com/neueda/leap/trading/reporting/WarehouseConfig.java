package com.neueda.leap.trading.reporting;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class WarehouseConfig {
    /*
     * IMPORTANT: do not expose the warehouse DataSource as a bean named simply
     * "dataSource" or as @Primary. Spring Boot must continue to auto-configure
     * its primary datasource from spring.datasource.* for JPA/Flyway/OLTP.
     * This secondary datasource is used only by the explicitly qualified
     * warehouse JdbcTemplate.
     */
    @Bean(name = "warehouseDataSource", defaultCandidate = false)
    DataSource warehouseDataSource(
            @Value("${app.warehouse.url:jdbc:postgresql://localhost:55433/trading_dw}") String url,
            @Value("${app.warehouse.username:trading_dw}") String username,
            @Value("${app.warehouse.password:trading_dw_change_me}") String password) {
        return DataSourceBuilder.create().url(url).username(username).password(password)
                .driverClassName("org.postgresql.Driver").build();
    }

    @Bean(name = "warehouseJdbcTemplate", defaultCandidate = false)
    JdbcTemplate warehouseJdbcTemplate(@Qualifier("warehouseDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
