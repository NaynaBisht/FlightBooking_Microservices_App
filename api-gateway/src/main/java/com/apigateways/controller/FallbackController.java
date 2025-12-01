package com.apigateways.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class FallbackController {

	@RequestMapping("/fallback/flight")
	public Mono<String> flightServiceFallback() {
		return Mono.just("Flight Service is currently unavailable. Please try again later.");
	}

	@RequestMapping("/fallback/booking")
	public Mono<String> bookingServiceFallback() {
		return Mono.just("Booking Service is taking longer than expected. Please try again later.");
	}
}