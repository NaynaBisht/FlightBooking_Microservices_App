package com.booking.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.booking.dto.FlightDTO;
import com.booking.dto.FlightSearchRequest;
import com.booking.dto.FlightSearchResponse;

import reactor.core.publisher.Mono;

@Component
public class FlightClient {

    @Autowired
    private WebClient.Builder webClientBuilder;

    public Mono<FlightDTO> findByFlightNumber(String flightNumber) {
        return webClientBuilder.build()
                .get()
                .uri("http://flight-service/api/flight/" + flightNumber)
                .retrieve()
                .bodyToMono(FlightDTO.class);
    }
}
