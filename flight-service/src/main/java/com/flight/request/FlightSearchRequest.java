package com.flight.request;

import java.time.LocalDate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FlightSearchRequest {

	@NotBlank(message = "Departing airport is required")
	private String departingAirport;

	@NotBlank(message = "Arrival airport is required")
	private String arrivalAirport;

	@NotNull(message = "Departure date is required")
	private LocalDate departDate;

	@NotNull(message = "Passenger details are required")
	private PassengerCount passengers;
}
