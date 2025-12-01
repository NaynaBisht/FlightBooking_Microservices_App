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
				.route("booking-service",
						r -> r.path("/api/flight/booking/**", "/api/flight/ticket/**").uri("lb://BOOKING-SERVICE"))

				// Route 2: Flight Service (Dynamic Load Balancing)
				.route("flight-service", r -> r.path("/api/flight/**").uri("lb://FLIGHT-SERVICE")).build();

	}
}