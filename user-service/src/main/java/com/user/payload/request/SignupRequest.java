package com.user.payload.request;

import java.util.Set;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {

	@NotBlank
	@Size(min = 3, max = 20)
	private String username;

	@NotBlank
	@Size(max = 50)
	@Email
	private String email;

	@NotBlank
	@JsonProperty("firstName")
	private String firstName;

	@NotBlank
	@JsonProperty("lastName")
	private String lastName;

	@NotBlank
	@Size(min = 10, max = 15)
	@JsonProperty("mobileNumber")
	private String mobileNumber;

	private Set<String> role;

	@NotBlank
	@Size(min = 6, max = 40)
	private String password;
}