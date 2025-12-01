package com.booking.test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import com.booking.client.FlightClient;
import com.booking.config.RabbitMQConfig;
import com.booking.dto.FlightDTO;
import com.booking.entity.Booking;
import com.booking.entity.Passenger;
import com.booking.repository.BookingRepository;
import com.booking.request.BookingRequest;
import com.booking.request.PassengerRequest;
import com.booking.service.BookingService;
import com.booking.service.PnrGeneratorService;
import com.common.dto.BadRequestException;
import com.common.dto.ResourceNotFoundException;
import com.common.dto.SeatUnavailableException;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock private FlightClient flightClient;
    @Mock private BookingRepository bookingRepository;
    @Mock private PnrGeneratorService pnrGeneratorService;
    @Mock private RabbitTemplate rabbitTemplate;

    @InjectMocks private BookingService bookingService;

    // --- Helper Method ---
    private FlightDTO createFlightDTO(String flightNumber, int seats, float price) {
        FlightDTO dto = new FlightDTO();
        dto.setFlightNumber(flightNumber);
        dto.setDepartingAirport("DEL");
        dto.setArrivalAirport("BOM");
        dto.setAvailableSeats(seats);
        dto.setPrice(price);
        return dto;
    }

    @Test
    void bookFlight_Success() {
        // Arrange
        String flightNum = "AI101";

        FlightDTO flight = createFlightDTO(flightNum, 100, 5000);

        BookingRequest req = createRequest(1);

        Booking savedBooking = new Booking();
        savedBooking.setPnr("PNR123");
        savedBooking.setPrice(5000);
        savedBooking.setTotalPrice(5000); // FIXED: consistent calculation
        savedBooking.setEmailId(req.getEmailId());
        savedBooking.setFlightNumber(flightNum);

        Passenger p = new Passenger();
        p.setPassengerName("John");
        savedBooking.setPassengers(List.of(p));

        // Mock dependencies
        when(flightClient.findByFlightNumber(flightNum)).thenReturn(Mono.just(flight));
        when(pnrGeneratorService.generatePnr()).thenReturn("PNR123");
        when(bookingRepository.save(any(Booking.class))).thenReturn(Mono.just(savedBooking));

        // IMPORTANT FIX FOR VOID METHOD
        doNothing().when(rabbitTemplate).convertAndSend(
                anyString(), anyString(), any(Object.class));

        // Act & Assert
        StepVerifier.create(bookingService.bookFlight(flightNum, req))
                .expectNextMatches(res ->
                        res.getPnr().equals("PNR123") &&
                        res.getTotalPrice() == 5000.0
                )
                .verifyComplete();

        // Verify RabbitMQ was triggered
        verify(rabbitTemplate).convertAndSend(
                eq(RabbitMQConfig.EXCHANGE),
                eq(RabbitMQConfig.ROUTING_KEY),
                any(Object.class)
        );
    }

    @Test
    void getBookingByPnr_Success() {
        Booking b = new Booking();
        b.setPnr("PNR123");

        when(bookingRepository.findByPnr("PNR123")).thenReturn(Mono.just(b));

        StepVerifier.create(bookingService.getBookingByPnr("PNR123"))
                .expectNext(b)
                .verifyComplete();
    }

    @Test
    void cancelBooking_Success() {
        Booking b = new Booking();
        b.setStatus("BOOKED");

        when(bookingRepository.findByPnr("PNR123")).thenReturn(Mono.just(b));
        when(bookingRepository.save(b)).thenReturn(Mono.just(b));

        StepVerifier.create(bookingService.cancelBooking("PNR123"))
                .verifyComplete();
    }

    private BookingRequest createRequest(int passengers) {
        BookingRequest req = new BookingRequest();
        req.setEmailId("test@mail.com");
        req.setContactNumber("9999999999");
        req.setNumberOfSeats(passengers);

        PassengerRequest p = new PassengerRequest();
        p.setPassengerName("John");
        p.setAge(20);
        p.setGender("Male");
        p.setMealPref("Veg");
        p.setSeatNum("A1");   // FIXED

        req.setPassengers(Collections.singletonList(p));
        return req;
    }
    @Test
    void bookFlight_FlightNotFound() {
        BookingRequest req = createRequest(1);

        when(flightClient.findByFlightNumber("AI404"))
                .thenReturn(Mono.empty()); // triggers ResourceNotFoundException

        StepVerifier.create(bookingService.bookFlight("AI404", req))
                .expectErrorMatches(ex ->
                        ex instanceof ResourceNotFoundException &&
                        ex.getMessage().contains("Flight not found"))
                .verify();
    }
    @Test
    void bookFlight_InsufficientSeats() {
        BookingRequest req = createRequest(2); // wants 2 seats

        FlightDTO flight = createFlightDTO("AI101", 1, 5000f); // only 1 seat

        when(flightClient.findByFlightNumber("AI101")).thenReturn(Mono.just(flight));

        StepVerifier.create(bookingService.bookFlight("AI101", req))
                .expectErrorMatches(ex -> 
                    ex instanceof SeatUnavailableException &&
                    ex.getMessage().contains("Insufficient seat availability"))
                .verify();
    }
    @Test
    void bookFlight_PassengerCountMismatch() {
        BookingRequest req = createRequest(1);
        req.setNumberOfSeats(2); // mismatch

        FlightDTO flight = createFlightDTO("AI101", 5, 5000);

        when(flightClient.findByFlightNumber("AI101")).thenReturn(Mono.just(flight));

        StepVerifier.create(bookingService.bookFlight("AI101", req))
                .expectErrorMatches(ex ->
                        ex instanceof BadRequestException &&
                        ex.getMessage().contains("Passenger count must match"))
                .verify();
    }

    @Test
    void getBookingByPnr_NotFound() {
        when(bookingRepository.findByPnr("BAD123")).thenReturn(Mono.empty());

        StepVerifier.create(bookingService.getBookingByPnr("BAD123"))
                .expectErrorMatches(ex ->
                        ex instanceof ResourceNotFoundException &&
                        ex.getMessage().contains("Booking not found"))
                .verify();
    }
    @Test
    void cancelBooking_NotFound() {
        when(bookingRepository.findByPnr("BADPNR")).thenReturn(Mono.empty());

        StepVerifier.create(bookingService.cancelBooking("BADPNR"))
                .expectErrorMatches(ex ->
                        ex instanceof ResourceNotFoundException &&
                        ex.getMessage().contains("Booking not found"))
                .verify();
    }

}
