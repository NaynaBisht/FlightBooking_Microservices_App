package com.flight.test;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.flight.FlightServiceApplication;

@SpringBootTest
class FlightServiceApplicationTest {

    @Test
    void contextLoads() {
        FlightServiceApplication.main(new String[] {});
    }
}