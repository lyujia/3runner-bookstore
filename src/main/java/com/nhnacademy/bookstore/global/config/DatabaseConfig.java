package com.nhnacademy.bookstore.global.config;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.nhnacademy.bookstore.global.config.properties.DbProperties;
import com.nhnacademy.bookstore.global.keymanager.manager.KeyManager;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(DataSourceProperties.class)
public class DatabaseConfig {
	private final KeyManager keyManager;
	private final DbProperties dbProperties;

	@Bean
	public DataSource dataSource() {
		BasicDataSource basicDataSource = new BasicDataSource();

		basicDataSource.setDriverClassName(dbProperties.getDriver());
		basicDataSource.setUrl(keyManager.getValue(dbProperties.getUrl()));
		basicDataSource.setUsername(keyManager.getValue(dbProperties.getUsername()));
		basicDataSource.setPassword(keyManager.getValue(dbProperties.getPassword()));

		basicDataSource.setInitialSize(dbProperties.getInitialSize());
		basicDataSource.setMaxTotal(dbProperties.getMaxTotal());
		basicDataSource.setMaxIdle(dbProperties.getMaxIdle());
		basicDataSource.setMinIdle(dbProperties.getMinIdle());

		basicDataSource.setTestOnBorrow(true);
		basicDataSource.setValidationQuery("SELECT 1");

		return basicDataSource;
	}
}
