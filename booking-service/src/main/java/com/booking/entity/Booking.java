package com.booking.entity;

import java.time.LocalDateTime;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Document(collection = "bookings")
public class Booking {

	@Id
	private String id;
	private String flightNumber;

	private String pnr;

	@NotBlank(message = "Email is required")
	@Email(message = "Invalid email format")
	private String emailId;

	@NotBlank(message = "Contact number is required")
	@Pattern(regexp = "^[6-9]\\d{9}$")
	private String contactNumber;

	@NotNull
	@Min(1)
	private int numberOfSeats;

	private LocalDateTime bookingTimestamp = LocalDateTime.now();
	private float price;
	private float totalPrice;

	private List<Passenger> passengers;

	private String status = "BOOKED";
}
