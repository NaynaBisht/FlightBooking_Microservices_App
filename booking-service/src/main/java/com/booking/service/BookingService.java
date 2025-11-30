package com.booking.service;

import java.time.LocalDateTime;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.booking.client.FlightClient;
import com.booking.entity.Booking;
import com.booking.entity.Passenger;
import com.booking.enums.Gender;
import com.booking.enums.MealPreference;
import com.booking.exception.BadRequestException;
import com.booking.exception.PassengerValidationException;
import com.booking.exception.ResourceNotFoundException;
import com.booking.exception.SeatUnavailableException;
import com.booking.repository.BookingRepository;
import com.booking.request.BookingRequest;
import com.booking.request.PassengerRequest;
import com.booking.response.BookingResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {

	@Autowired
	private FlightClient flightClient;
	private final BookingRepository bookingRepository;
	private final PnrGeneratorService pnrGeneratorService;

	public Mono<BookingResponse> bookFlight(String flightNumber, BookingRequest request) {

		return flightClient.findByFlightNumber(flightNumber)
				.switchIfEmpty(
						Mono.error(new ResourceNotFoundException("Flight not found for number: " + flightNumber)))
				.flatMap(flight -> {

					log.debug("Fetched flight details: {}", flight);

					if (flight.getAvailableSeats() < request.getNumberOfSeats()) {
						throw new SeatUnavailableException("Insufficient seat availability");
					}

					if (!request.getNumberOfSeats().equals(request.getPassengers().size())) {
						throw new BadRequestException("Passenger count must match number of seats booked");
					}

					float price = flight.getPrice();
					float tax = price * 0.18f;
					float totalPrice = (price + tax) * request.getNumberOfSeats();

					Booking booking = new Booking();
					booking.setFlightNumber(flightNumber);
					booking.setPnr(pnrGeneratorService.generatePnr());
					booking.setEmailId(request.getEmailId());
					booking.setContactNumber(request.getContactNumber());
					booking.setBookingTimestamp(LocalDateTime.now());
					booking.setNumberOfSeats(request.getNumberOfSeats());
					booking.setPrice(price);
					booking.setTotalPrice(totalPrice);
					booking.setPassengers(
							request.getPassengers().stream().map(this::toPassenger).collect(Collectors.toList()));
					booking.setStatus("BOOKED");

					flight.setAvailableSeats(flight.getAvailableSeats() - request.getNumberOfSeats());

					return bookingRepository.save(booking)
							.map(saved -> new BookingResponse(saved.getPnr(), price, "Booking successful"));
				});
	}

	private Passenger toPassenger(PassengerRequest req) {
		Passenger p = new Passenger();
		p.setPassengerName(req.getPassengerName());
		p.setAge(req.getAge());

		try {
			p.setGender(Gender.valueOf(req.getGender().toUpperCase()));
		} catch (IllegalArgumentException e) {
			throw new PassengerValidationException("Invalid gender value: " + req.getGender());
		}

		p.setSeatNum(req.getSeatNum());

		try {
			p.setMealPref(MealPreference.valueOf(req.getMealPref().toUpperCase().replace("-", "_")));
		} catch (IllegalArgumentException e) {
			throw new PassengerValidationException("Invalid meal preference: " + req.getMealPref());
		}

		return p;
	}

	public Mono<Booking> getBookingByPnr(String pnr) {
		return bookingRepository.findByPnr(pnr)
				.switchIfEmpty(Mono.error(new ResourceNotFoundException("Booking not found for PNR: " + pnr)));
	}

	public Flux<Booking> getBookingHistoryByEmailId(String emailId) {
		return bookingRepository.findByEmailId(emailId);
	}

	public Mono<Void> cancelBooking(String pnr) {
		return bookingRepository.findByPnr(pnr)
				.switchIfEmpty(Mono.error(new ResourceNotFoundException("Booking not found"))).flatMap(booking -> {
					booking.setStatus("CANCELLED");
					return bookingRepository.save(booking);
				}).then();
	}
}
