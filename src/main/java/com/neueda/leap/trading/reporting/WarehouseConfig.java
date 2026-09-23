package com.neueda.leap.trading.reporting;
import javax.sql.DataSource; import org.apache.ibatis.session.SqlSessionFactory; import org.mybatis.spring.SqlSessionFactoryBean; import org.mybatis.spring.annotation.MapperScan; import org.springframework.beans.factory.annotation.*; import org.springframework.boot.jdbc.DataSourceBuilder; import org.springframework.context.annotation.*;
/** Configures the separate reporting warehouse MyBatis session. */
@Configuration
@MapperScan(basePackages="com.neueda.leap.trading.reporting",sqlSessionFactoryRef="warehouseSqlSessionFactory")
public class WarehouseConfig {
 @Bean(name="warehouseDataSource",defaultCandidate=false) DataSource warehouseDataSource(@Value("${app.warehouse.url:jdbc:postgresql://localhost:55433/trading_dw}")String url,@Value("${app.warehouse.username:trading_dw}")String username,@Value("${app.warehouse.password:trading_dw_change_me}")String password){return DataSourceBuilder.create().url(url).username(username).password(password).driverClassName("org.postgresql.Driver").build();}
 @Bean(name="warehouseSqlSessionFactory") SqlSessionFactory warehouseSqlSessionFactory(@Qualifier("warehouseDataSource")DataSource ds)throws Exception{SqlSessionFactoryBean bean=new SqlSessionFactoryBean();bean.setDataSource(ds);bean.getObject();return bean.getObject();}
}