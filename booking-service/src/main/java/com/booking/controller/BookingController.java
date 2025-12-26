package com.booking.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.booking.entity.Booking;
import com.booking.request.BookingRequest;
import com.booking.response.BookingResponse;
import com.booking.service.BookingService;
import com.common.dto.ResourceNotFoundException;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("api/flight")
@RequiredArgsConstructor
public class BookingController {

	private final BookingService bookingService;

	@GetMapping("/ticket/{pnr}")
	public Mono<ResponseEntity<Booking>> getTicketDetailsByPnr(@PathVariable String pnr) {
		log.info("Fetching ticket details for PNR={}", pnr);

		return bookingService.getBookingByPnr(pnr).map(ResponseEntity::ok).onErrorResume(ex -> {
			log.error("Error fetching ticket for PNR {}: {}", pnr, ex.getMessage());
			String msg = ex.getMessage() == null ? "" : ex.getMessage();

			if (msg.contains("not found")) {
				return Mono.just(ResponseEntity.notFound().build());
			}

			return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
		});
	}

	@GetMapping("/booking/history/{emailId}")
	public Mono<ResponseEntity<List<Booking>>> getBookingHistory(@PathVariable String emailId) {
		log.info("Fetching booking history for emailId={}", emailId);

		return bookingService.getBookingHistoryByEmailId(emailId).collectList()
				.map(bookings -> ResponseEntity.ok(bookings)).onErrorResume(ex -> {
					log.error("Error fetching booking history: {}", ex.getMessage());
					return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.emptyList()));
				});
	}

	@PostMapping("/booking/{flightNumber}")
	public Mono<ResponseEntity<BookingResponse>> bookFlight(@PathVariable String flightNumber,
			@Valid @RequestBody BookingRequest request) {

		log.info("Booking flight {} for {}", flightNumber, request.getEmailId());
		return bookingService.bookFlight(flightNumber, request)
				.map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response)).onErrorResume(ex -> {

					String message = ex.getMessage() != null ? ex.getMessage() : "Unexpected error";

					if (message.contains("Flight not found")) {
						return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
								.body(new BookingResponse(
										null,
										0,
										message,
										request.getEmailId(),
										request.getPassengers().get(0).getPassengerName(),
										flightNumber,
										null,
										null,
										null)));
					}

					return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
							.body(new BookingResponse(
									null,
									0,
									message,
									request.getEmailId(),
									request.getPassengers().get(0).getPassengerName(),
									flightNumber,
									null,
									null,
									null)));
				});

	}

	@DeleteMapping("/booking/cancel/{pnr}")
public Mono<ResponseEntity<Object>> cancelBooking(@PathVariable String pnr) {
    log.warn("Cancel request received for PNR={}", pnr);

    return bookingService.cancelBooking(pnr)
            .then(Mono.just(ResponseEntity.noContent().build()))
            .onErrorResume(ex -> {
                log.error("Cancellation error for PNR {}: {}", pnr, ex.getMessage());

                if (ex instanceof ResourceNotFoundException) {
                    return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body("Booking not found for PNR: " + pnr));
                }

                if (ex instanceof IllegalStateException) {
                    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(ex.getMessage()));
                }
                return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("An unexpected error occurred. Please contact support."));
            });
}
}
