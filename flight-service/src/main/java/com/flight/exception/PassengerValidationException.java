package com.flight.exception;

public class PassengerValidationException extends RuntimeException {
	public PassengerValidationException(String message) {
		super(message);
	}
}
