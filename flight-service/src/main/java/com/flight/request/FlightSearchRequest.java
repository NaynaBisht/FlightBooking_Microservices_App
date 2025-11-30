package com.flight.request;

import java.time.LocalDate;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class FlightSearchRequest {

    @NotBlank(message = "Departing airport is required")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Departing airport must be a 3-letter uppercase IATA code")
    private String departingAirport;

    @NotBlank(message = "Arrival airport is required")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Arrival airport must be a 3-letter uppercase IATA code")
    private String arrivalAirport;

    @NotNull(message = "Departure date is required")
    private LocalDate departDate;

    @NotNull(message = "Passenger details are required")
    private PassengerCount passengers;
}
