package com.booking.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.booking.entity.Booking;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BookingRepository extends ReactiveMongoRepository<Booking, String> {
	Flux<Booking> findByFlightNumber(String flightNumber);

	Mono<Booking> findByPnr(String pnr);

	Flux<Booking> findByEmailId(String emailId);
}
