package com.flight.repository;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.flight.entity.Flight;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FlightRepository extends ReactiveMongoRepository<Flight, String> {

	Mono<Flight> findByFlightNumber(String flightNumber);

	Flux<Flight> findByDepartingAirportAndArrivalAirportAndDepartureTimeBetween(String departingAirport,
			String arrivalAirport, LocalDateTime start, LocalDateTime end);
}
