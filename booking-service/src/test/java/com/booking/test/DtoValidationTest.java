package com.booking.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.booking.request.BookingRequest;
import com.booking.request.PassengerCount;
import com.booking.request.PassengerRequest;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class DtoValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // --- PassengerCount Tests ---
    
    @Test
    void testPassengerCount_TotalCalculation() {
        PassengerCount count = new PassengerCount();
        count.setAdults(2);
        count.setChildren(1);
        
        assertEquals(3, count.getTotalPassengers(), "Total passengers should be sum of adults and children");
    }

    @Test
    void testPassengerCount_Validation() {
        PassengerCount count = new PassengerCount();
        count.setAdults(0); // Min is 1
        
        Set<ConstraintViolation<PassengerCount>> violations = validator.validate(count);
        assertFalse(violations.isEmpty(), "Should have validation error for 0 adults");
    }

    // --- BookingRequest Validation Tests ---

    @Test
    void testBookingRequest_ValidData() {
        BookingRequest req = new BookingRequest();
        req.setEmailId("test@example.com");
        req.setContactNumber("9876543210"); // Valid Indian mobile
        req.setNumberOfSeats(1);
        req.setPassengers(List.of(new PassengerRequest())); // Assuming PassengerRequest is valid or empty for this check

        // Note: If PassengerRequest has @NotNull constraints, you must populate it too
    }

    @Test
    void testBookingRequest_InvalidEmail() {
        BookingRequest req = new BookingRequest();
        req.setEmailId("invalid-email");
        
        Set<ConstraintViolation<BookingRequest>> violations = validator.validateProperty(req, "emailId");
        assertFalse(violations.isEmpty(), "Should fail on invalid email format");
        assertEquals("Invalid email format", violations.iterator().next().getMessage());
    }

    @Test
    void testBookingRequest_InvalidContactNumber() {
        BookingRequest req = new BookingRequest();
        req.setContactNumber("12345"); // Too short
        
        Set<ConstraintViolation<BookingRequest>> violations = validator.validateProperty(req, "contactNumber");
        assertFalse(violations.isEmpty(), "Should fail on invalid contact number");
        
        req.setContactNumber("5555555555"); // Does not start with 6-9
        violations = validator.validateProperty(req, "contactNumber");
        assertFalse(violations.isEmpty(), "Should fail if number doesn't start with 6-9");
    }
}