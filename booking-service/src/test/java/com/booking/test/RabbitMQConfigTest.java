package com.booking.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;

import com.booking.config.RabbitMQConfig;

class RabbitMQConfigTest {

    private final RabbitMQConfig config = new RabbitMQConfig();

    @Test
    void testConstants() {
        assertEquals("email_queue", RabbitMQConfig.QUEUE);
        assertEquals("email_exchange", RabbitMQConfig.EXCHANGE);
        assertEquals("email_routing_key", RabbitMQConfig.ROUTING_KEY);
    }

    @Test
    void testBeanCreation() {
        Queue queue = config.emailQueue();
        assertNotNull(queue);
        assertEquals("email_queue", queue.getName());

        TopicExchange exchange = config.emailExchange();
        assertNotNull(exchange);
        assertEquals("email_exchange", exchange.getName());

        Binding binding = config.binding(queue, exchange);
        assertNotNull(binding);
        assertEquals("email_routing_key", binding.getRoutingKey());
        
        Jackson2JsonMessageConverter converter = (Jackson2JsonMessageConverter) config.converter();
        assertNotNull(converter);
    }
}