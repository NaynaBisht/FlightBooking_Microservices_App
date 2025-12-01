package com.flight.test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.flight.controller.FlightSearchController;
import com.flight.entity.Flight;
import com.flight.request.FlightSearchRequest;
import com.flight.request.PassengerCount;
import com.flight.response.FlightSearchResponse;
import com.flight.response.FlightSearchResponse.FlightInfo;
import com.flight.service.FlightService;

import reactor.core.publisher.Mono;

@WebFluxTest(FlightSearchController.class)
class FlightSearchControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private FlightService flightService;

    @Test
    void searchFlights_Success() {
        FlightInfo info = new FlightInfo();
        info.setFlightNumber("AI101");

        FlightSearchResponse response =
                new FlightSearchResponse(1, List.of(info), "Success");

        when(flightService.searchFlights(any(FlightSearchRequest.class)))
                .thenReturn(Mono.just(response));

        FlightSearchRequest req = new FlightSearchRequest();
        req.setDepartingAirport("DEL");
        req.setArrivalAirport("BOM");
        req.setDepartDate(LocalDate.now().plusDays(1));
        req.setPassengers(new PassengerCount(1, 0));

        webTestClient.post().uri("/api/flight/search")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Success")
                .jsonPath("$.totalFlights").isEqualTo(1);
    }
    
    @Test
    void getFlightByNumber_Success() {
        // Use Entity instead of DTO
        Flight flight = new Flight();
        flight.setFlightNumber("AI101");
        flight.setPrice(5000);
        flight.setDepartureTime(LocalDateTime.now());
        flight.setArrivalTime(LocalDateTime.now().plusHours(2));
        
        when(flightService.getFlightByNumber("AI101")).thenReturn(Mono.just(flight));
        
        webTestClient.get().uri("/api/flight/AI101")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.flightNumber").isEqualTo("AI101")
            .jsonPath("$.price").isEqualTo(5000.0);
    }
}