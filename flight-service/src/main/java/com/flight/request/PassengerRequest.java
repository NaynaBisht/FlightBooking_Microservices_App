package com.flight.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class PassengerRequest {

	@NotBlank(message = "Passenger name is required")
	private String passengerName;

	@NotNull(message = "Passenger age is required")
	@Min(value = 1, message = "Age must be at least 1")
	private Integer age;

	@NotBlank(message = "Gender is required")
	@Pattern(regexp = "(?i)Male|Female|Other")
	private String gender;

	@NotBlank(message = "Seat number is required")
	@Pattern(regexp = "^[A-Z]\\d{1,2}$", message = "Seat number must be like A1, B12")
	private String seatNum;

	@NotBlank(message = "Meal preference is required")
	@Pattern(regexp = "(?i)Veg|Non-Veg|No-Meal")
	private String mealPref;

}
