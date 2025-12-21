package com.booking.test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.booking.controller.BookingController;
import com.booking.entity.Booking;
import com.booking.request.BookingRequest;
import com.booking.request.PassengerRequest;
import com.booking.response.BookingResponse;
import com.booking.service.BookingService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@WebFluxTest(BookingController.class)
class BookingControllerTest {

        @Autowired
        private WebTestClient webTestClient;

        @MockitoBean
        private BookingService bookingService;

        @Test
        void getTicketDetails_Success() {
                Booking mockBooking = new Booking();
                mockBooking.setPnr("PNR123");

                when(bookingService.getBookingByPnr("PNR123"))
                                .thenReturn(Mono.just(mockBooking));

                webTestClient.get().uri("/api/flight/ticket/PNR123")
                                .exchange()
                                .expectStatus().isOk()
                                .expectBody()
                                .jsonPath("$.pnr").isEqualTo("PNR123");
        }

        @Test
        void getTicketDetails_Failure() {
                // ANY exception (not containing "not found") returns 500
                when(bookingService.getBookingByPnr("BAD123"))
                                .thenReturn(Mono.error(new RuntimeException("DB error")));

                webTestClient.get().uri("/api/flight/ticket/BAD123")
                                .exchange()
                                .expectStatus().is5xxServerError();
        }

        @Test
        void getTicketDetails_Empty() {
                // Mono.empty() => default WebFlux behavior => HTTP 200 OK
                when(bookingService.getBookingByPnr("EMPTY1"))
                                .thenReturn(Mono.empty());

                webTestClient.get().uri("/api/flight/ticket/EMPTY1")
                                .exchange()
                                .expectStatus().isOk(); // MUST be OK (controller does NOT handle empty case)
        }

        @Test
        void getBookingHistory_Success() {
                Booking b1 = new Booking();
                b1.setEmailId("test@mail.com");

                when(bookingService.getBookingHistoryByEmailId("test@mail.com"))
                                .thenReturn(Flux.just(b1));

                webTestClient.get().uri("/api/flight/booking/history/test@mail.com")
                                .exchange()
                                .expectStatus().isOk()
                                .expectBodyList(Booking.class).hasSize(1);
        }

        @Test
        void getBookingHistory_Failure() {
                // Controller returns 400 BAD_REQUEST on errors
                when(bookingService.getBookingHistoryByEmailId("err@mail.com"))
                                .thenReturn(Flux.error(new RuntimeException("DB failed")));

                webTestClient.get().uri("/api/flight/booking/history/err@mail.com")
                                .exchange()
                                .expectStatus().isBadRequest(); // MUST be 400 based on controller
        }

        @Test
        void getBookingHistory_Empty() {
                when(bookingService.getBookingHistoryByEmailId("empty@mail.com"))
                                .thenReturn(Flux.empty());

                webTestClient.get().uri("/api/flight/booking/history/empty@mail.com")
                                .exchange()
                                .expectStatus().isOk()
                                .expectBodyList(Booking.class).hasSize(0);
        }

        @Test
        void bookFlight_Success() {
                BookingResponse response = new BookingResponse(
                                "PNR123",
                                100f,
                                "Success",
                                "test@mail.com",
                                "John",
                                "FL1",
                                "SRI",
                                "AMR", 
                                "10:00 AM"
                );

                when(bookingService.bookFlight(anyString(), any()))
                                .thenReturn(Mono.just(response));

                BookingRequest req = createValidRequest();

                webTestClient.post().uri("/api/flight/booking/FL1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(req)
                                .exchange()
                                .expectStatus().isCreated()
                                .expectBody()
                                .jsonPath("$.pnr").isEqualTo("PNR123")
                                // Optional: Verify the new fields in the response
                                .jsonPath("$.departingAirport").isEqualTo("SRI")
                                .jsonPath("$.arrivalAirport").isEqualTo("AMR")
                                .jsonPath("$.departureTime").isEqualTo("10:00 AM");
        }

        @Test
        void bookFlight_Failure() {
                BookingRequest req = createValidRequest();

                when(bookingService.bookFlight(anyString(), any()))
                                .thenReturn(Mono.error(new RuntimeException("Service Down")));

                webTestClient.post().uri("/api/flight/booking/FL1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(req)
                                .exchange()
                                .expectStatus().isBadRequest(); // MUST be 400 based on controller
        }

        @Test
        void cancelBooking_Success() {
                when(bookingService.cancelBooking("PNR123"))
                                .thenReturn(Mono.empty());

                webTestClient.delete().uri("/api/flight/booking/cancel/PNR123")
                                .exchange()
                                .expectStatus().isNoContent();
        }

        @Test
        void cancelBooking_Failure() {
                when(bookingService.cancelBooking("ERR123"))
                                .thenReturn(Mono.error(new RuntimeException("Something bad")));

                webTestClient.delete().uri("/api/flight/booking/cancel/ERR123")
                                .exchange()
                                .expectStatus().isEqualTo(HttpStatus.CONFLICT); // fixed
        }

        private BookingRequest createValidRequest() {
                BookingRequest req = new BookingRequest();
                req.setEmailId("test@mail.com");
                req.setContactNumber("9876543210");
                req.setNumberOfSeats(1);

                PassengerRequest p = new PassengerRequest();
                p.setPassengerName("John");
                p.setAge(25);
                p.setGender("Male");
                p.setMealPref("Veg");
                p.setSeatNum("A1");

                req.setPassengers(List.of(p));
                return req;
        }

        @Test
        void getTicketDetails_NotFoundMessage() {
                when(bookingService.getBookingByPnr("NF123"))
                                .thenReturn(Mono.error(new RuntimeException("Booking not found for PNR")));

                webTestClient.get().uri("/api/flight/ticket/NF123")
                                .exchange()
                                .expectStatus().isNotFound();
        }

        @Test
        void bookFlight_FlightNotFoundMessage() {
                BookingRequest req = createValidRequest();

                when(bookingService.bookFlight(anyString(), any()))
                                .thenReturn(Mono.error(new RuntimeException("Flight not found")));

                webTestClient.post().uri("/api/flight/booking/FL1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(req)
                                .exchange()
                                .expectStatus().isNotFound(); // EXACTLY THIS
        }

        @Test
        void cancelBooking_NotFoundMessage() {
                when(bookingService.cancelBooking("NF999"))
                                .thenReturn(Mono.error(new RuntimeException("not found")));

                webTestClient.delete().uri("/api/flight/booking/cancel/NF999")
                                .exchange()
                                .expectStatus().isNotFound();
        }

}
