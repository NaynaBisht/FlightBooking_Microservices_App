package com.flight.test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.flight.controller.AirlineController;
import com.flight.entity.Flight;
import com.flight.request.AddFlightRequest;
import com.flight.service.AirlineService;

import reactor.core.publisher.Mono;

@WebFluxTest(AirlineController.class)
class AirlineControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private AirlineService airlineService;

    @Test
    void addFlight_Success() {
        // Arrange
        Flight savedFlight = new Flight();
        savedFlight.setFlightNumber("AI101");
        savedFlight.setAirlineName("Air India");

        when(airlineService.addFlight(any(AddFlightRequest.class))).thenReturn(Mono.just(savedFlight));

        AddFlightRequest request = new AddFlightRequest();
        request.setFlightNumber("AI101");
        request.setAirlineName("Air India");
        request.setDepartingAirport("DEL");
        request.setArrivalAirport("BOM");
        request.setDepartureTime(LocalDateTime.now().plusHours(2));
        request.setArrivalTime(LocalDateTime.now().plusHours(4));
        request.setTotalSeats(100);
        request.setPrice(5000);

        // Act & Assert
        webTestClient.post().uri("/api/flight/airline/inventory/add")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.flightNumber").isEqualTo("AI101");
    }

    @Test
    void addFlight_ValidationError() {
        // Test @Valid - Missing flight number
        AddFlightRequest request = new AddFlightRequest();
        // Missing required fields...

        webTestClient.post().uri("/api/flight/airline/inventory/add")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest(); 
    }
}