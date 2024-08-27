package com.nhnacademy.bookstore.global.config.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "rabbitmq")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RabbitMqProperties {
	String host;
	String port;
	String username;
	String password;
}
