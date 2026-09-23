package com.neueda.leap.trading;

import org.mybatis.spring.annotation.MapperScan;
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
@MapperScan(
    basePackages = "com.neueda.leap.trading",
    annotationClass = org.apache.ibatis.annotations.Mapper.class,
    excludeFilters = @org.springframework.context.annotation.ComponentScan.Filter(
        type = org.springframework.context.annotation.FilterType.REGEX,
        pattern = "com\\.neueda\\.leap\\.trading\\.reporting\\..*"
    )
)
public class TradingApplication {
    public static void main(String[] args) {
        SpringApplication.run(TradingApplication.class, args);
    }
}
