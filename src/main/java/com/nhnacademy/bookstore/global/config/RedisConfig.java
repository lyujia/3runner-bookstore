package com.nhnacademy.bookstore.global.config;

import com.nhnacademy.bookstore.global.config.properties.RedisProperties;
import com.nhnacademy.bookstore.global.keymanager.manager.KeyManager;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(RedisProperties.class)
public class RedisConfig {
	private final RedisProperties redisProperties;
	private final KeyManager keyManager;

	@Bean
	RedisConnectionFactory redisConnectionFactory() {
		RedisStandaloneConfiguration redisStandaloneConfiguration =
			new RedisStandaloneConfiguration(keyManager.getValue(redisProperties.getHost()), Integer.parseInt(keyManager.getValue(redisProperties.getPort())));
		redisStandaloneConfiguration.setDatabase(Integer.parseInt(keyManager.getValue(redisProperties.getDatabase())));
		redisStandaloneConfiguration.setPassword(keyManager.getValue(redisProperties.getPassword()));
		return new LettuceConnectionFactory(redisStandaloneConfiguration);
	}

	@Bean
	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
		RedisTemplate<String, Object> sessionRedisTemplate = new RedisTemplate<>();
		sessionRedisTemplate.setConnectionFactory(redisConnectionFactory);

		sessionRedisTemplate.setKeySerializer(new StringRedisSerializer());
		sessionRedisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());

		sessionRedisTemplate.setHashKeySerializer(new StringRedisSerializer());
		sessionRedisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

		sessionRedisTemplate.afterPropertiesSet();
		return sessionRedisTemplate;
	}
}
