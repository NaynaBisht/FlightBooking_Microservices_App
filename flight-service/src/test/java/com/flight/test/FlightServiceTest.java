package com.flight.test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.flight.entity.Flight;
import com.flight.repository.FlightRepository;
import com.flight.request.FlightSearchRequest;
import com.flight.request.PassengerCount;
import com.flight.response.FlightSearchResponse;
import com.flight.service.FlightService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootTest
class FlightServiceTest {

    @MockitoBean
    private FlightRepository flightRepository;

    @Autowired
    private FlightService flightService;

    @Test
    void testSearchFlightsSuccess() {
        FlightSearchRequest request = new FlightSearchRequest();
        request.setDepartingAirport("DEL");
        request.setArrivalAirport("BLR");
        request.setDepartDate(LocalDate.now());

        PassengerCount passengers = new PassengerCount();
        passengers.setAdults(2);
        passengers.setChildren(1);
        request.setPassengers(passengers);

        Flight flight = new Flight();
        flight.setId("101");
        flight.setFlightNumber("AI203");
        flight.setAirlineName("Air India");
        flight.setDepartingAirport("DEL");
        flight.setArrivalAirport("BLR");
        flight.setDepartureTime(LocalDateTime.now().plusHours(2));
        flight.setArrivalTime(LocalDateTime.now().plusHours(4));
        flight.setPrice(5000);
        flight.setTotalSeats(180);
        flight.setAvailableSeats(50);

        when(flightRepository.findByDepartingAirportAndArrivalAirportAndDepartureTimeBetween(any(), any(), any(),
                any())).thenReturn(Flux.just(flight));

        FlightSearchResponse response = flightService.searchFlights(request).block();

        assertEquals(1, response.getTotalFlights());
        assertEquals(1, response.getFlights().size());

        FlightSearchResponse.FlightInfo result = response.getFlights().get(0);
        assertEquals("AI203", result.getFlightNumber());
        assertEquals("Air India", result.getAirlineName());
    }

    @Test
    void testSearchFlightsNoResults() {
        FlightSearchRequest request = new FlightSearchRequest();
        request.setDepartingAirport("DEL");
        request.setArrivalAirport("BLR");
        request.setDepartDate(LocalDate.now());
        request.setPassengers(new PassengerCount(1, 0));

        when(flightRepository.findByDepartingAirportAndArrivalAirportAndDepartureTimeBetween(any(), any(), any(),
                any())).thenReturn(Flux.empty());

        FlightSearchResponse response = flightService.searchFlights(request).block();
        assertEquals(0, response.getTotalFlights());
    }
    
    @Test
    void testGetFlightByNumber_Success() {
        Flight flight = new Flight();
        flight.setFlightNumber("AI999");
        flight.setPrice(4000);
        flight.setAvailableSeats(10);
        
        when(flightRepository.findByFlightNumber("AI999")).thenReturn(Mono.just(flight));
        
        // CHANGED: Returns Flight, not FlightDTO
        Flight result = flightService.getFlightByNumber("AI999").block();
        
        assertNotNull(result);
        assertEquals("AI999", result.getFlightNumber());
        assertEquals(4000.0, result.getPrice());
    }
}