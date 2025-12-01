package com.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDTO {
	private String pnr;
	private float totalPrice;
	private String message;
	private String emailId;     
    private String passengerName; 
    private String flightNumber;
}
