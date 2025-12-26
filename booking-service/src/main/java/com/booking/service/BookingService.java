package com.booking.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.booking.client.FlightClient;
import com.booking.config.RabbitMQConfig;
import com.booking.entity.Booking;
import com.booking.entity.Passenger;
import com.booking.enums.Gender;
import com.booking.enums.MealPreference;
import com.booking.repository.BookingRepository;
import com.booking.request.BookingRequest;
import com.booking.request.PassengerRequest;
import com.booking.response.BookingResponse;
import com.common.dto.BadRequestException;
import com.common.dto.PassengerValidationException;
import com.common.dto.ResourceNotFoundException;
import com.common.dto.SeatUnavailableException;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {

	private final FlightClient flightClient;
	private final BookingRepository bookingRepository;
	private final PnrGeneratorService pnrGeneratorService;
	private final RabbitTemplate rabbitTemplate;

	public Mono<BookingResponse> bookFlight(String flightNumber, BookingRequest request) {

		return bookingRepository.findByFlightNumberAndEmailId(flightNumber, request.getEmailId())
				.flatMap(existing -> Mono.error(new BadRequestException("You have already booked this flight")))

				.then(

						flightClient.findByFlightNumber(flightNumber)
								.switchIfEmpty(Mono.error(
										new ResourceNotFoundException("Flight not found for number: " + flightNumber)))
								.flatMap(flight -> {

									log.debug("Fetched flight details: {}", flight);

									if (flight.getAvailableSeats() < request.getNumberOfSeats()) {
										throw new SeatUnavailableException("Insufficient seat availability");
									}

									if (!request.getNumberOfSeats().equals(request.getPassengers().size())) {
										throw new BadRequestException(
												"Passenger count must match number of seats booked");
									}

									float price = flight.getPrice();
									float tax = price * 0.18f;
									float totalPrice = (price + tax) * request.getNumberOfSeats();

									Booking booking = new Booking();
									booking.setFlightNumber(flightNumber);
									booking.setPnr(pnrGeneratorService.generatePnr());

									booking.setDepartingAirport(flight.getDepartingAirport());
									booking.setArrivalAirport(flight.getArrivalAirport());
									booking.setDepartDate(flight.getDepartDate());
									booking.setDepartureTime(flight.getDepartureTime());
									booking.setArrivalTime(flight.getArrivalTime());

									booking.setEmailId(request.getEmailId());
									booking.setContactNumber(request.getContactNumber());
									booking.setBookingTimestamp(LocalDateTime.now());
									booking.setNumberOfSeats(request.getNumberOfSeats());
									booking.setPrice(price);
									booking.setTotalPrice(totalPrice);
									booking.setPassengers(request.getPassengers().stream().map(this::toPassenger)
											.collect(Collectors.toList()));
									booking.setStatus("BOOKED");

									// flight.setAvailableSeats(flight.getAvailableSeats() -
									// request.getNumberOfSeats());

									return flightClient
											.reduceSeats(flightNumber, request.getNumberOfSeats())
											.then(
													bookingRepository.save(booking)
															.doOnSuccess(savedBooking -> {
																try {
																	Map<String, Object> emailMessage = new HashMap<>();
																	emailMessage.put("pnr", savedBooking.getPnr());
																	emailMessage.put("emailId",
																			savedBooking.getEmailId());
																	emailMessage.put("flightNumber",
																			savedBooking.getFlightNumber());
																	emailMessage.put("totalPrice",
																			savedBooking.getTotalPrice());

																	String name = savedBooking.getPassengers()
																			.get(0)
																			.getPassengerName();
																	emailMessage.put("passengerName", name);

																	rabbitTemplate.convertAndSend(
																			RabbitMQConfig.EXCHANGE,
																			RabbitMQConfig.ROUTING_KEY,
																			emailMessage);

																	log.info(
																			"Message sent to Queue for PNR: {}",
																			savedBooking.getPnr());

																} catch (Exception e) {
																	log.error("Failed to send RabbitMQ message", e);
																}
															}))
											.map(saved -> new BookingResponse(
													saved.getPnr(),
													saved.getTotalPrice(),
													"Booking successful",
													saved.getEmailId(),
													saved.getPassengers().get(0).getPassengerName(),
													saved.getFlightNumber(),
													saved.getDepartingAirport(),
													saved.getArrivalAirport(),
													saved.getDepartureTime()));
								}));
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
				.switchIfEmpty(Mono.error(new ResourceNotFoundException("Booking not found")))
				.flatMap(booking -> {
					try {
						OffsetDateTime departureDateTime = OffsetDateTime.parse(booking.getDepartureTime());

						// 2. Get the current time in UTC to match your DB timezone
						OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);

						long hoursDifference = Duration.between(now, departureDateTime).toHours();

						if (hoursDifference < 24) {
							return Mono.error(new IllegalStateException(
									"Cancellations are not allowed within 24 hours of departure."));
						}

						booking.setStatus("CANCELLED");
						return bookingRepository.save(booking);

					} catch (Exception e) {
						return Mono.error(new IllegalArgumentException("Error parsing departure time format."));
					}
				})
				.then();
	}
}
