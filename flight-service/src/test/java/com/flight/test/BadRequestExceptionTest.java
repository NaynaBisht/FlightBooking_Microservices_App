package com.flight.test;

import org.junit.jupiter.api.Test;

import com.common.dto.BadRequestException;

import static org.assertj.core.api.Assertions.assertThat;

class BadRequestExceptionTest {

	@Test
	void testMessageStored() {
		BadRequestException ex = new BadRequestException("Bad request");
		assertThat(ex.getMessage()).isEqualTo("Bad request");
	}
}
