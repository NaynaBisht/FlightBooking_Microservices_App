package com.flight.service;

import java.time.LocalDateTime;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.common.dto.InvalidFlightTimeException;
import com.flight.entity.Flight;
import com.flight.repository.FlightRepository;
import com.flight.request.AddFlightRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class AirlineService {

	private final FlightRepository flightRepository;

	public Mono<Flight> addFlight(AddFlightRequest request) {

		log.info("Received request to add new flight: {}", request.getFlightNumber());

		if (request.getDepartureTime().isBefore(LocalDateTime.now().plusHours(1))) {
			throw new InvalidFlightTimeException("Departure time must be at least 1 hour in the future");
		}

		if (!request.getArrivalTime().isAfter(request.getDepartureTime())) {
			throw new InvalidFlightTimeException("Arrival time must be after departure time");
		}

		Flight flight = new Flight();
		BeanUtils.copyProperties(request, flight);
		flight.setAvailableSeats(request.getTotalSeats());

		return flightRepository.save(flight).doOnSuccess(f -> log.info("Flight added: {}", f.getFlightNumber()));
	}
}
