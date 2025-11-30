package com.booking.service;

import java.security.SecureRandom;

import org.springframework.stereotype.Service;

@Service
public class PnrGeneratorService {
	private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	private static final int PNR_LENGTH = 6;
	private final SecureRandom secureRandom = new SecureRandom();

	public String generatePnr() {
		StringBuilder pnrBuilder = new StringBuilder(PNR_LENGTH);
		for (int i = 0; i < PNR_LENGTH; i++) {
			int randomIndex = secureRandom.nextInt(CHARACTERS.length());
			pnrBuilder.append(CHARACTERS.charAt(randomIndex));
		}
		return pnrBuilder.toString();
	}
}
