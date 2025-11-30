package com.booking.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class PassengerCount {

	@Min(1)
	private int adults;

	@Min(0)
	private int children;

	public int getTotalPassengers() {
		return adults + children;
	}

}
