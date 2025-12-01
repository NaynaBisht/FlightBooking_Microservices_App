package com.apiGateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;

@Configuration
public class GatewayConfig {

	@Bean
	public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
		return builder.routes()
				// Route 1: Booking Service (Dynamic Load Balancing)
				.route("booking-service", r -> r.path("/api/flight/booking/**", "/api/flight/ticket/**")
						.filters(f -> f.circuitBreaker(
								c -> c.setName("bookingCircuitBreaker").setFallbackUri("forward:/fallback/booking"))) // fails
						.uri("lb://BOOKING-SERVICE"))

				// Route 2: Flight Service with Circuit Breaker
				.route("flight-service", r -> r.path("/api/flight/**")
						.filters(f -> f.circuitBreaker(
								c -> c.setName("flightCircuitBreaker").setFallbackUri("forward:/fallback/flight")))
						.uri("lb://FLIGHT-SERVICE"))
				.build();
	}
}