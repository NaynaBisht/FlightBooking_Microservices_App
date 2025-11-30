package com.booking.entity;

import com.booking.enums.Gender;
import com.booking.enums.MealPreference;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class Passenger {

	@NotBlank
	private String passengerName;

	@NotNull
	private int age;

	private Gender gender;

	@NotBlank
	@Pattern(regexp = "^[A-Z]\\d{1,2}$")
	private String seatNum;

	private MealPreference mealPref;
}
