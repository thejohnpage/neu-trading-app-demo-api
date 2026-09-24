package com.neueda.leap.trading;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Entry point for the trading API.
 *
 * <p>The primary mapper scan covers the OLTP database. The reporting package is
 * excluded here because it is scanned separately by WarehouseConfig using the
 * warehouse SqlSessionFactory.</p>
 */
@SpringBootApplication
@EnableScheduling
public class TradingApplication {
    public static void main(String[] args) {
        SpringApplication.run(TradingApplication.class, args);
    }
}
