package com.booking.test;

import com.booking.client.FlightClient;
import com.booking.dto.FlightDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class FlightClientTest {

    private FlightClient flightClient;
    private ExchangeFunction exchangeFunction;

    @BeforeEach
    void setup() {
        exchangeFunction = Mockito.mock(ExchangeFunction.class);

        WebClient webClient = WebClient.builder()
                .exchangeFunction(exchangeFunction)
                .baseUrl("http://flight-service")
                .build();

        // Inject WebClient using the constructor
        this.flightClient = new FlightClient(WebClient.builder().exchangeFunction(exchangeFunction));
    }

    @Test
    void testFindByFlightNumber_Success() {

        String jsonResponse = "{\"flightNumber\":\"AI101\", \"price\":5500}";

        ClientResponse mockResponse =
                ClientResponse.create(HttpStatus.OK)
                        .header("Content-Type", "application/json")
                        .body(jsonResponse)
                        .build();

        when(exchangeFunction.exchange(any())).thenReturn(Mono.just(mockResponse));

        StepVerifier.create(flightClient.findByFlightNumber("AI101"))
                .expectNextMatches(dto ->
                        dto.getFlightNumber().equals("AI101") &&
                        dto.getPrice() == 5500
                )
                .verifyComplete();
    }

    @Test
    void testFindByFlightNumber_NotFound() {

        ClientResponse mockResponse =
                ClientResponse.create(HttpStatus.NOT_FOUND)
                        .build();

        when(exchangeFunction.exchange(any())).thenReturn(Mono.just(mockResponse));

        StepVerifier.create(flightClient.findByFlightNumber("XX123"))
                .expectComplete()       // Should return Mono.empty()
                .verify();
    }

    @Test
    void testFindByFlightNumber_ServerError() {

        ClientResponse mockResponse =
                ClientResponse.create(HttpStatus.INTERNAL_SERVER_ERROR)
                        .build();

        when(exchangeFunction.exchange(any())).thenReturn(Mono.just(mockResponse));

        StepVerifier.create(flightClient.findByFlightNumber("AI101"))
                .expectError()      // retrieve() will convert 500 into WebClientResponseException
                .verify();
    }
}
