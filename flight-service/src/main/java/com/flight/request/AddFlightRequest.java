package com.flight.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddFlightRequest {

	@NotBlank(message = "Flight number is required")
	private String flightNumber;

	@NotBlank(message = "Airline name is required")
	private String airlineName;

	@NotBlank(message = "Departing airport code is required")
	private String departingAirport;

	@NotBlank(message = "Arrival airport code is required")
	private String arrivalAirport;

	@NotNull(message = "Departure time is required")
	@Future(message = "Departure time must be in the future")
	private LocalDateTime departureTime;

	@NotNull(message = "Arrival time is required")
	private LocalDateTime arrivalTime;

	@Min(value = 100, message = "Price must be at least ₹100")
	private float price;

	@Min(1)
	private int totalSeats;
}
