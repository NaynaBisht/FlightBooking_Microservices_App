package com.flight.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Document(collection = "flights")
public class Flight {

	@Id
	private String id;

	@NotBlank
	@Indexed(unique = true)
	private String flightNumber;

	@NotBlank
	private String airlineName;

	@NotBlank
	@Pattern(regexp = "^[A-Z]{3}$", message = "Airport code must be 3 uppercase letters")
	private String departingAirport;

	@NotBlank
	@Pattern(regexp = "^[A-Z]{3}$", message = "Airport code must be 3 uppercase letters")
	private String arrivalAirport;

	@NotNull
	private LocalDateTime departureTime;

	@NotNull
	private LocalDateTime arrivalTime;

	@Min(100)
	private float price;

	@Min(5)
	private int totalSeats;

	private int availableSeats;
}
