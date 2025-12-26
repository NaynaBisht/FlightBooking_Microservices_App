package com.booking.client;

import org.springframework.stereotype.Component;

import org.springframework.web.reactive.function.client.WebClient;
import com.booking.dto.FlightDTO;
import reactor.core.publisher.Mono;

@Component
public class FlightClient {

	private final WebClient webClient;

	public FlightClient(WebClient.Builder webClientBuilder) {
		this.webClient = webClientBuilder.baseUrl("http://flight-service").build();
	}

	public Mono<FlightDTO> findByFlightNumber(String flightNumber) {
		return webClient.get().uri("/api/flight/{flightNumber}", flightNumber).retrieve()
				.onStatus(status -> status.value() == 404, response -> Mono.empty()).bodyToMono(FlightDTO.class);
	}

	public Mono<Void> reduceSeats(String flightNumber, int seats) {
		return webClient.put()
				.uri("/api/flight/{flightNumber}/reduce-seats/{seats}",
						flightNumber, seats)
				.retrieve()
				.bodyToMono(Void.class);
	}
}