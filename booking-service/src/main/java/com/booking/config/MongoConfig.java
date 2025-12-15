package com.booking.config;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongoConfig {

	@Value("${spring.data.mongodb.uri}")
	private String mongoUri;

	@Bean
	public MongoClient mongoClient() {
		// Explicitly forcing the connection for booking-service
		System.out.println("BOOKING-SERVICE: Forcing MongoDB connection to: " + mongoUri);
		return MongoClients.create(mongoUri);
	}
}