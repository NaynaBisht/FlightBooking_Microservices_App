package com.notification.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "notificationExchange";

    // OTP Configuration
    public static final String OTP_QUEUE = "otp_email_queue";
    public static final String OTP_ROUTING_KEY = "otp.email";

    // Booking Configuration (Standardizing with your other services)
    public static final String BOOKING_QUEUE = "email_queue";
    public static final String BOOKING_ROUTING_KEY = "emailRoutingKey";

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue otpQueue() {
        return new Queue(OTP_QUEUE, true);
    }

    @Bean
    public Queue bookingQueue() {
        return new Queue(BOOKING_QUEUE, true);
    }

    @Bean
    public Binding otpBinding() {
        return BindingBuilder.bind(otpQueue()).to(exchange()).with(OTP_ROUTING_KEY);
    }

    @Bean
    public Binding bookingBinding() {
        return BindingBuilder.bind(bookingQueue()).to(exchange()).with(BOOKING_ROUTING_KEY);
    }

    @Bean
    public MessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }
}