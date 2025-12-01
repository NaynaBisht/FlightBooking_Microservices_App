package com.common.dto;

public class SeatUnavailableException extends RuntimeException {
	public SeatUnavailableException(String message) {
		super(message);
	}
}
