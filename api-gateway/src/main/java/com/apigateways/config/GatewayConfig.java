package com.apigateways.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.apigateways.filter.AuthenticationFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;

@Configuration
public class GatewayConfig {

	private final AuthenticationFilter authFilter;

	public GatewayConfig(AuthenticationFilter authFilter) {
		this.authFilter = authFilter;
	}

	@Bean
	public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
		return builder.routes()
				// Route 1: Booking Service
				.route("booking-service", r -> r.path("/api/flight/booking/**", "/api/flight/ticket/**")
						.filters(f -> f.filter(authFilter.apply(new AuthenticationFilter.Config())).circuitBreaker(
								c -> c.setName("bookingCircuitBreaker").setFallbackUri("forward:/fallback/booking")))
						.uri("lb://BOOKING-SERVICE"))

				// Route 2: Flight Service
				.route("flight-service", r -> r
						.path("/api/flight/airline/**", "/api/flight/search/**", "/api/flight/**")
						.filters(f -> f.filter(authFilter.apply(new AuthenticationFilter.Config())).circuitBreaker(
								c -> c.setName("flightCircuitBreaker").setFallbackUri("forward:/fallback/flight")))
						.uri("lb://FLIGHT-SERVICE"))

				// USER-SERVICE – auth endpoints (public)
				.route("user-auth", r -> r.path("/api/auth/**").uri("lb://USER-SERVICE"))

				// USER-SERVICE – test endpoints (secured)
				.route("user-test",
						r -> r.path("/api/test/**")
								.filters(f -> f.filter(authFilter.apply(new AuthenticationFilter.Config())))
								.uri("lb://USER-SERVICE"))

				.build();
	}
}