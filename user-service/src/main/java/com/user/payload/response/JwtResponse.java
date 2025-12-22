package com.user.payload.response;

import java.util.List;
import lombok.Data;

@Data
public class JwtResponse {
	private String token;
	private String type = "Bearer";
	private String id;
	private String username;
	private String email;
	private List<String> roles;
	private boolean mustChangePassword;

	public JwtResponse(String accessToken, String id, String username, String email, List<String> roles, boolean mustChangePassword) {
		this.token = accessToken;
		this.id = id;
		this.username = username;
		this.email = email;
		this.roles = roles;
		this.mustChangePassword = mustChangePassword;
	}
}