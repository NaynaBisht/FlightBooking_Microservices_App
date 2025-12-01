package com.booking.test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.booking.service.PnrGeneratorService;

class PnrGeneratorServiceTest {

	private final PnrGeneratorService service = new PnrGeneratorService();

	@Test
	void generatePnr_ShouldReturn6Chars() {
		String pnr = service.generatePnr();
		assertNotNull(pnr);
		assertEquals(6, pnr.length());
		// Verify it only contains Allowed Characters (A-Z, 0-9)
		assertTrue(pnr.matches("^[A-Z0-9]+$"));
	}
}
