package com.flight.request;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PassengerCount {

	@Min(1)
	private int adults;

	@Min(0)
	private int children;

	public int getTotalPassengers() {
		return adults + children;
	}

}
