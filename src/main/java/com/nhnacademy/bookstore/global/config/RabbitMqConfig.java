package com.nhnacademy.bookstore.global.config;

import com.nhnacademy.bookstore.global.config.properties.RabbitMqProperties;
import com.nhnacademy.bookstore.global.keymanager.manager.KeyManager;
import com.nhnacademy.bookstore.purchase.coupon.messageListener.CouponAnotherMessageListener;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(RabbitMqProperties.class)
public class RabbitMqConfig {

    private final KeyManager keyManager;
    private final RabbitMqProperties rabbitMqProperties;


    private static final String queueName1 = "3RUNNER-COUPON-ISSUED";
    private static final String queueName2 = "3RUNNER-COUPON-EXPIRED-IN-THREE-DAY";
    private static final String exchangeName ="3RUNNER-EXCHANGE-NAME";
    private static final String routingKey1="3RUNNER-ROUTING-KEY";
    private static final String routingKey2 = "3RUNNER-ROUTING-KEY2";

    @Bean
    public TopicExchange couponExchange() {
        return new TopicExchange(exchangeName);
    }

    @Bean
    public Queue expiredCouponQueue() {
        return new Queue(queueName1, false);
    }

    @Bean
    public Queue expiredThreeDayCouponQueue() {
        return new Queue(queueName2, false);
    }

    @Bean
    public Binding bindingExpiredCouponQueue() {
        return BindingBuilder.bind(expiredCouponQueue()).to(couponExchange()).with(routingKey1);
    }

    @Bean
    public Binding bindingExpiredThreeDayCouponQueue() {
        return BindingBuilder.bind(expiredThreeDayCouponQueue()).to(couponExchange()).with(routingKey2);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public MessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(keyManager.getValue(rabbitMqProperties.getHost()));
        connectionFactory.setPort(Integer.parseInt(keyManager.getValue(rabbitMqProperties.getPort())));
        connectionFactory.setUsername(keyManager.getValue(rabbitMqProperties.getUsername()));
        connectionFactory.setPassword(keyManager.getValue(rabbitMqProperties.getPassword()));
        return connectionFactory;
    }

    @Bean
    public SimpleMessageListenerContainer expiredThreeDayCouponListenerContainer(ConnectionFactory connectionFactory,
                                                                                 @Qualifier("expiredThreeDayCouponListenerAdapter") MessageListenerAdapter expiredThreeDayCouponListenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(queueName2);
        container.setMessageListener(expiredThreeDayCouponListenerAdapter);
        return container;
    }

    @Bean(name = "expiredThreeDayCouponListenerAdapter")
    public MessageListenerAdapter listenerAdapter2(CouponAnotherMessageListener expiredThreeDayCouponListenerAdapter) {
        return new MessageListenerAdapter(expiredThreeDayCouponListenerAdapter, "쿠폰 만료일이 3일 남았습니다.");
    }
}