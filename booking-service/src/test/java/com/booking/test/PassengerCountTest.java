package com.booking.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.booking.dto.PassengerCount;

class PassengerCountTest {

    @Test
    void testGetTotalPassengers() {
        PassengerCount pc = new PassengerCount();
        pc.setAdults(2);
        pc.setChildren(1);

        assertEquals(3, pc.getTotalPassengers());
    }

    @Test
    void testGetTotalPassengers_ZeroChildren() {
        PassengerCount pc = new PassengerCount();
        pc.setAdults(3);
        pc.setChildren(0);

        assertEquals(3, pc.getTotalPassengers());
    }

    @Test
    void testGetTotalPassengers_OnlyOneAdult() {
        PassengerCount pc = new PassengerCount();
        pc.setAdults(1);
        pc.setChildren(0);

        assertEquals(1, pc.getTotalPassengers());
    }
}
