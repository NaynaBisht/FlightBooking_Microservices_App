package com.booking.test;

import org.junit.jupiter.api.Test;

import com.common.dto.InvalidFlightTimeException;

import static org.assertj.core.api.Assertions.assertThat;

class InvalidFlightTimeExceptionTest {

	@Test
	void testMessageStored() {
		InvalidFlightTimeException ex = new InvalidFlightTimeException("Invalid timing");
		assertThat(ex.getMessage()).isEqualTo("Invalid timing");
	}
}
