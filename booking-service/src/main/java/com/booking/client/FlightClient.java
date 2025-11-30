package com.booking.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import com.booking.dto.FlightDTO;
import reactor.core.publisher.Mono;

@Component
public class FlightClient {

    private final WebClient webClient;

    // Constructor Injection (Best Practice)
    // The builder passed here is the one from your Config class (which has @LoadBalanced)
    public FlightClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("http://flight-service") // Set the Base URL here
                .build();
    }

    public Mono<FlightDTO> findByFlightNumber(String flightNumber) {
        return webClient.get()
                // Now you only need the specific path
                .uri("/api/flight/{flightNumber}", flightNumber) 
                .retrieve()
                .bodyToMono(FlightDTO.class);
    }
}