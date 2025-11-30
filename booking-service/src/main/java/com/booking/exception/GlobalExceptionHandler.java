package com.booking.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(InvalidFlightTimeException.class)
	public Mono<ResponseEntity<Map<String, Object>>> handleInvalidFlightTime(InvalidFlightTimeException ex) {
		return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(errorResponse(HttpStatus.BAD_REQUEST, ex.getMessage())));
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public Mono<ResponseEntity<Map<String, Object>>> handleNotFound(ResourceNotFoundException ex) {
		return Mono.just(
				ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse(HttpStatus.NOT_FOUND, ex.getMessage())));
	}

	@ExceptionHandler(SeatUnavailableException.class)
	public Mono<ResponseEntity<Map<String, Object>>> handleSeatUnavailable(SeatUnavailableException ex) {
		return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(errorResponse(HttpStatus.BAD_REQUEST, ex.getMessage())));
	}

	@ExceptionHandler(BadRequestException.class)
	public Mono<ResponseEntity<Map<String, Object>>> handleBadRequest(BadRequestException ex) {
		return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(errorResponse(HttpStatus.BAD_REQUEST, ex.getMessage())));
	}

	@ExceptionHandler(WebExchangeBindException.class)
	public Mono<ResponseEntity<Map<String, Object>>> handleValidationException(WebExchangeBindException ex) {
		// Extract field errors to show which fields failed
		String errors = ex.getBindingResult().getFieldErrors().stream().map(FieldError::getDefaultMessage)
				.collect(Collectors.joining(", "));

		return Mono.just(
				ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse(HttpStatus.BAD_REQUEST, errors)));
	}

	@ExceptionHandler(Exception.class)
	public Mono<ResponseEntity<Map<String, Object>>> handleGeneral(Exception ex) {
		return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong")));
	}

	private Map<String, Object> errorResponse(HttpStatus status, String message) {
		return Map.of("status", status.value(), "error", status.getReasonPhrase(), "message", message, "timestamp",
				LocalDateTime.now().toString());
	}
}
