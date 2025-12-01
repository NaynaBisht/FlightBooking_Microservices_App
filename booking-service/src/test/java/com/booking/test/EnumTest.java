package com.booking.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.booking.enums.Gender;
import com.booking.enums.MealPreference;

class EnumTest {

	@Test
	void testGenderEnum() {
		assertEquals(3, Gender.values().length);
		assertEquals(Gender.MALE, Gender.valueOf("MALE"));
		assertEquals(Gender.FEMALE, Gender.valueOf("FEMALE"));
		assertEquals(Gender.OTHER, Gender.valueOf("OTHER"));
	}

	@Test
	void testMealPreferenceEnum() {
		assertEquals(3, MealPreference.values().length);
		assertEquals(MealPreference.VEG, MealPreference.valueOf("VEG"));
		assertEquals(MealPreference.NON_VEG, MealPreference.valueOf("NON_VEG"));
		assertEquals(MealPreference.NO_MEAL, MealPreference.valueOf("NO_MEAL"));
	}
}
