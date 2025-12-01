package com.booking.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.common.dto.GlobalExceptionHandler;
import com.common.dto.ResourceNotFoundException;
import com.common.dto.SeatUnavailableException;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleResourceNotFoundException() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Not Found");
        Mono<ResponseEntity<Map<String, Object>>> response = handler.handleNotFound(ex);

        StepVerifier.create(response)
            .expectNextMatches(res -> 
                res.getStatusCode() == HttpStatus.NOT_FOUND &&
                res.getBody().get("message").equals("Not Found")
            )
            .verifyComplete();
    }

    @Test
    void handleSeatUnavailableException() {
        SeatUnavailableException ex = new SeatUnavailableException("No Seats");
        Mono<ResponseEntity<Map<String, Object>>> response = handler.handleSeatUnavailable(ex);

        StepVerifier.create(response)
            .expectNextMatches(res -> 
                res.getStatusCode() == HttpStatus.BAD_REQUEST &&
                res.getBody().get("message").equals("No Seats")
            )
            .verifyComplete();
    }
    
    // Add similar tests for InvalidFlightTimeException, etc.
}