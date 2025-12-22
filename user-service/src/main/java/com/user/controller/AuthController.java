package com.user.controller;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.user.models.ERole;
import com.user.models.Role;
import com.user.models.User;
import com.user.payload.request.LoginRequest;
import com.user.payload.request.PasswordUpdateRequest;
import com.user.payload.request.SignupRequest;
import com.user.payload.response.JwtResponse;
import com.user.payload.response.MessageResponse;
import com.user.repository.RoleRepository;
import com.user.repository.UserRepository;
import com.user.security.jwt.JwtUtils;
import com.user.security.services.UserDetailsImpl;
import java.time.temporal.ChronoUnit;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	JwtUtils jwtUtils;

	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);

		String jwt = jwtUtils.generateJwtToken(authentication);

		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

		User user = userRepository.findByUsername(userDetails.getUsername())
				.orElseThrow(() -> new RuntimeException("Error: User not found."));

		boolean mustChangePassword = false;
		if (user.getLastPasswordChangeDate() == null) {
			mustChangePassword = true;
		} else {
			long daysSinceChange = ChronoUnit.DAYS.between(user.getLastPasswordChangeDate(), LocalDateTime.now());
			if (daysSinceChange >= 90) {
				mustChangePassword = true;
			}
		}

		List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
				.collect(Collectors.toList());

		return ResponseEntity.ok(
				new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), roles,
						mustChangePassword));
	}

	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
		// 1. Check duplicates
		if (userRepository.existsByUsername(signUpRequest.getUsername())) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
		}
		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
		}

		// 2. Create User
		User user = new User();
		user.setUsername(signUpRequest.getUsername());
		user.setEmail(signUpRequest.getEmail());
		user.setPassword(encoder.encode(signUpRequest.getPassword()));

		user.setFirstName(signUpRequest.getFirstName() != null ? signUpRequest.getFirstName() : "Unknown");
		user.setLastName(signUpRequest.getLastName() != null ? signUpRequest.getLastName() : "User");
		user.setMobileNumber(signUpRequest.getMobileNumber());

		Set<String> strRoles = signUpRequest.getRole();
		Set<Role> roles = new HashSet<>();

		if (strRoles == null) {
			Role userRole = roleRepository.findByName(ERole.ROLE_USER)
					.orElseGet(() -> roleRepository.save(new Role(ERole.ROLE_USER)));
			roles.add(userRole);
		} else {
			strRoles.forEach(role -> {
				switch (role) {
					case "admin":
						Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
								.orElseGet(() -> roleRepository.save(new Role(ERole.ROLE_ADMIN)));
						roles.add(adminRole);
						break;
					default:
						Role userRole = roleRepository.findByName(ERole.ROLE_USER)
								.orElseGet(() -> roleRepository.save(new Role(ERole.ROLE_USER)));
						roles.add(userRole);
				}
			});
		}

		user.setRoles(roles);
		userRepository.save(user);

		return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
	}

	@PostMapping("/change-password")
	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	public ResponseEntity<?> updatePassword(@Valid @RequestBody PasswordUpdateRequest request) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName();

		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new RuntimeException("Error: User not found."));
		if (!encoder.matches(request.getCurrentPassword(), user.getPassword())) {
			return ResponseEntity
					.status(HttpStatus.BAD_REQUEST)
					.body(new MessageResponse("Error: Current password is incorrect."));
		}
		user.setPassword(encoder.encode(request.getNewPassword()));
		user.setLastPasswordChangeDate(LocalDateTime.now());

		userRepository.save(user);

		return ResponseEntity.ok(new MessageResponse("Password updated successfully!"));
	}
}