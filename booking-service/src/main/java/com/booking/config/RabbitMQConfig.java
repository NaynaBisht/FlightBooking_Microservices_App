package com.booking.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
	public static final String QUEUE = "email_queue";
	public static final String EXCHANGE = "email_exchange";
	public static final String ROUTING_KEY = "email_routing_key";

	@Bean
	public Queue emailQueue() {
		return new Queue(QUEUE);
	}

	@Bean
	public TopicExchange emailExchange() {
		return new TopicExchange(EXCHANGE);
	}

	@Bean
	public Binding binding(Queue emailQueue, TopicExchange emailExchange) {
		return BindingBuilder.bind(emailQueue).to(emailExchange).with(ROUTING_KEY);
	}

	@Bean
	public MessageConverter converter() {
		return new Jackson2JsonMessageConverter();
	}
}