package com.booking.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class FlightDTO {
	private String id;
	private String flightNumber;
	private String airlineName;
	private String departingAirport;
	private String arrivalAirport;
	private LocalDate departDate;
	private String departureTime;
	private String arrivalTime;
	private float price;
	private int availableSeats;
}
