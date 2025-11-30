package com.booking.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlightSearchResponse {

	private int totalFlights;
	private List<FlightInfo> flights;

	@Data
	public static class FlightInfo {
		private String id;
		private String flightNumber;
		private String airlineName;
		private String departingAirport;
		private String arrivalAirport;

		private String departureTime;
		private String arrivalTime;
		private String duration;
		private float price;

		private int availableSeats;
	}

	private String message;
}
