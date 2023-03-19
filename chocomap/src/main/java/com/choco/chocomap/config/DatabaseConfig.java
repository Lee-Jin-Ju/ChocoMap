package com.choco.chocomap.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
public class DatabaseConfig {
	
	@Value("${h2.datasource.url}")
    private String url;

	@Value("${h2.datasource.driver-class-name}")
	private String driverClassName;
	
    @Value("${h2.datasource.username}")
    private String username;

    @Value("${h2.datasource.password}")
    private String password;
    
	@Bean
    @Qualifier("H2DataSource")
    public DataSource H2DataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);

        return dataSource;
    }

    @Bean
    @Qualifier("H2JdbcTemplate")
    public JdbcTemplate H2JdbcTemplate(@Qualifier("H2DataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}

