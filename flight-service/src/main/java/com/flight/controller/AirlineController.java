package com.flight.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
	public Mono<ResponseEntity<Flight>> addFlight(@Valid @RequestBody AddFlightRequest request) {
		log.info("Adding flight: {}", request.getFlightNumber());

		return airlineService.addFlight(request)
				.map(savedFlight -> ResponseEntity.status(HttpStatus.CREATED).body(savedFlight));
	}
}
