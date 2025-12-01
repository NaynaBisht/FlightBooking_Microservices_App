package com.flight.test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.flight.entity.Flight;
import com.flight.repository.FlightRepository;
import com.flight.request.AddFlightRequest;
import com.flight.service.AirlineService;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class AirlineServiceTest {

    @Mock
    private FlightRepository flightRepository;

    @InjectMocks
    private AirlineService airlineService;

    @Test
    void addFlight_Success() {
        AddFlightRequest req = new AddFlightRequest();
        req.setFlightNumber("AI101");
        req.setDepartureTime(LocalDateTime.now().plusHours(2));
        req.setArrivalTime(LocalDateTime.now().plusHours(4));
        req.setTotalSeats(100);

        when(flightRepository.save(any(Flight.class))).thenAnswer(i -> Mono.just(i.getArgument(0)));

        StepVerifier.create(airlineService.addFlight(req))
                .expectNextMatches(f ->
                        f.getFlightNumber().equals("AI101") &&
                        f.getAvailableSeats() == 100
                )
                .verifyComplete();
    }
}
