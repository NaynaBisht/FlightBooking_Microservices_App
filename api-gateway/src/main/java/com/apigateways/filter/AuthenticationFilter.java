package com.apigateways.filter;

import com.apigateways.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

	@Autowired
	private RouteValidator validator;

	@Autowired
	private JwtUtil jwtUtil;

	public AuthenticationFilter() {
		super(Config.class);
	}

	@Override
	public GatewayFilter apply(Config config) {
		return ((exchange, chain) -> {
			if (validator.isSecured.test(exchange.getRequest())) {

				// --- FIX START ---

				// 2. Check for Header (Safe way for Spring Boot 3)
				// Instead of containsKey, we just get the header value directly
				if (exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION) == null) {
					throw new RuntimeException("Missing Authorization Header");
				}

				// 3. Extract Token
				// We know it's not null now, so we can get(0) safely
				String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);

				if (authHeader != null && authHeader.startsWith("Bearer ")) {
					authHeader = authHeader.substring(7);
				}

				// --- FIX END ---

				// 4. Validate Token
				try {
					// REST OF YOUR CODE IS CORRECT
					jwtUtil.validateToken(authHeader);
				} catch (Exception e) {
					System.out.println("Invalid Token Access!");
					throw new RuntimeException("Unauthorized Access: " + e.getMessage());
				}
			}
			return chain.filter(exchange);
		});
	}

	public static class Config {
		// Configuration properties can go here
	}
}