package com.flight.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.flight.entity.Flight;
import com.flight.request.FlightSearchRequest;
import com.flight.response.FlightSearchResponse;
import com.flight.service.FlightService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/flight")
@RequiredArgsConstructor
public class FlightSearchController {

	private final FlightService flightService;

	@PostMapping("/search")
	public Mono<ResponseEntity<FlightSearchResponse>> searchFlights(@Valid @RequestBody FlightSearchRequest request) {

		log.info("Searching flights: {} -> {} on {}", request.getDepartingAirport(), request.getArrivalAirport(),
				request.getDepartDate());

		return flightService.searchFlights(request).map(ResponseEntity::ok)
				.switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(new FlightSearchResponse(0, List.of(), "No flights found for selected date and route"))));
	}
	@GetMapping("/{flightNumber}")
    public Mono<ResponseEntity<Flight>> getFlightByNumber(@PathVariable String flightNumber) {
        log.info("Fetching details for flight number: {}", flightNumber);
        
        return flightService.getFlightByNumber(flightNumber)
                .map(flight -> ResponseEntity.ok(flight))
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).build()));
    }

}
