package com.booking.test;

import org.junit.jupiter.api.Test;

import com.common.dto.PassengerValidationException;

import static org.assertj.core.api.Assertions.assertThat;

class PassengerValidationExceptionTest {

	@Test
	void testMessageStored() {
		PassengerValidationException ex = new PassengerValidationException("Invalid passenger");
		assertThat(ex.getMessage()).isEqualTo("Invalid passenger");
	}
}
