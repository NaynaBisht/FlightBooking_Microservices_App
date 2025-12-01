package com.common.dto;

public class InvalidFlightTimeException extends RuntimeException {
	public InvalidFlightTimeException(String message) {
		super(message);
	}
}
