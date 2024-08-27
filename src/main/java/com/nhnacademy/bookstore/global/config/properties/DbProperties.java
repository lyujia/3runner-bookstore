package com.nhnacademy.bookstore.global.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Component
@ConfigurationProperties(prefix = "db")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DbProperties {
	private String driver;
	private String url;
	private String username;
	private String password;
	private int initialSize;
	private int maxTotal;
	private int maxIdle;
	private int minIdle;
	private long maxWait;
}
