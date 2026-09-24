package com.neueda.leap.trading.config;

import javax.sql.DataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configures MyBatis for the primary OLTP datasource.
 *
 * <p>Defining the primary SqlSessionFactory explicitly prevents the secondary
 * warehouse datasource from influencing mapper auto-configuration.</p>
 */
@Configuration
@MapperScan(
    basePackages = {
        "com.neueda.leap.trading.account",
        "com.neueda.leap.trading.admin",
        "com.neueda.leap.trading.cash",
        "com.neueda.leap.trading.client",
        "com.neueda.leap.trading.execution",
        "com.neueda.leap.trading.identity",
        "com.neueda.leap.trading.instrument",
        "com.neueda.leap.trading.marketdata",
        "com.neueda.leap.trading.order",
        "com.neueda.leap.trading.position"
    },
    sqlSessionFactoryRef = "sqlSessionFactory"
)
public class PrimaryMyBatisConfig {
 @Bean(name="sqlSessionFactory")
 SqlSessionFactory sqlSessionFactory(DataSource dataSource)throws Exception{
  SqlSessionFactoryBean bean=new SqlSessionFactoryBean();
  bean.setDataSource(dataSource);
  bean.setTypeHandlersPackage("com.neueda.leap.trading.config");
  org.apache.ibatis.session.Configuration config=new org.apache.ibatis.session.Configuration();
  config.setMapUnderscoreToCamelCase(true);
  bean.setConfiguration(config);
  return bean.getObject();
 }
}
