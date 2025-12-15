package com.flight.controller;

import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestHeader;

import com.flight.entity.Flight;
import com.flight.request.AddFlightRequest;
import com.flight.service.AirlineService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/flight/airline")
@RequiredArgsConstructor
public class AirlineController {

	private final AirlineService airlineService;

	@PostMapping("/inventory/add")
	public Mono<ResponseEntity<Flight>> addFlight(@Valid @RequestBody AddFlightRequest request,
			@RequestHeader(value = "loggedInUserRole", required = false) String role) {
		log.info("Request received from Role: " + role);
		if (role == null || !role.contains("ADMIN")) {
			return Mono.error(new RuntimeException("Access Denied: Admins Only"));
		}
		log.info("Adding flight: {}", request.getFlightNumber());
		return airlineService.addFlight(request)
				.flatMap(savedFlight -> Mono.just(ResponseEntity.status(HttpStatus.CREATED).body(savedFlight)));
	}
}
