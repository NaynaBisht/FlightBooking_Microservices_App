package com.booking.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {
	private String pnr;
	private float totalPrice;
	private String message;
	private String emailId;     
    private String passengerName; 
    private String flightNumber;
	private String departingAirport;
    private String arrivalAirport;
    private String departureTime;
}
