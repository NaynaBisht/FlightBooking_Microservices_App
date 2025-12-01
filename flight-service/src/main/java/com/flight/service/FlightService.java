package com.flight.service;

import java.time.LocalDate;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.common.dto.BadRequestException;
import com.flight.entity.Flight;
import com.flight.repository.FlightRepository;
import com.flight.request.FlightSearchRequest;
import com.flight.response.FlightSearchResponse;
import com.flight.response.FlightSearchResponse.FlightInfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class FlightService {

	private final FlightRepository flightRepository;

	public Mono<Flight> getFlightByNumber(String flightNumber) {
		return flightRepository.findByFlightNumber(flightNumber);
	}

	public Mono<FlightSearchResponse> searchFlights(FlightSearchRequest request) {

		if (request.getDepartingAirport().equals(request.getArrivalAirport())) {
			throw new BadRequestException("Departing and arrival airports cannot be the same");
		}

		if (request.getDepartDate().isBefore(LocalDate.now())) {
			throw new BadRequestException("Departure date cannot be in the past");
		}

		if (request.getPassengers().getTotalPassengers() < 1) {
			throw new BadRequestException("At least one passenger must be selected");
		}
		LocalDateTime startOfDay = request.getDepartDate().atStartOfDay();
		LocalDateTime endOfDay = request.getDepartDate().atTime(23, 59, 59);

		int passengers = request.getPassengers().getTotalPassengers();

		return flightRepository
				.findByDepartingAirportAndArrivalAirportAndDepartureTimeBetween(request.getDepartingAirport(),
						request.getArrivalAirport(), startOfDay, endOfDay)
				.filter(flight -> flight.getAvailableSeats() >= passengers
						&& flight.getDepartureTime().isAfter(LocalDateTime.now()))
				.map(this::toFlightInfo).collectList().map(list -> new FlightSearchResponse(list.size(), list,
						list.isEmpty() ? "No flights found" : "Success"));
	}

	private FlightInfo toFlightInfo(Flight flight) {
		FlightInfo info = new FlightInfo();
		info.setId(flight.getFlightNumber());
		info.setFlightNumber(flight.getFlightNumber());
		info.setAirlineName(flight.getAirlineName());
		info.setDepartingAirport(flight.getDepartingAirport());
		info.setArrivalAirport(flight.getArrivalAirport());
		info.setDepartureTime(flight.getDepartureTime().toString());
		info.setArrivalTime(flight.getArrivalTime().toString());
		long minutes = java.time.Duration.between(flight.getDepartureTime(), flight.getArrivalTime()).toMinutes();
		info.setDuration(minutes + " minutes");
		info.setPrice(flight.getPrice());
		info.setAvailableSeats(flight.getAvailableSeats());
		return info;
	}
}
