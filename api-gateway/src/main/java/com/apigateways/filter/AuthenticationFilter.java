package com.apigateways.filter;

import com.apigateways.util.JwtUtil;
import io.jsonwebtoken.Claims; // <--- Import this
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest; // <--- Import this
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

				// 1. Check for Header
				if (exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION) == null) {
					throw new RuntimeException("Missing Authorization Header");
				}

				// 2. Extract Token
				String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
				if (authHeader != null && authHeader.startsWith("Bearer ")) {
					authHeader = authHeader.substring(7);
				}

				try {
					// 3. Validate Token
					jwtUtil.validateToken(authHeader);

					// 4. Extract Claims (Roles & Username)
					Claims claims = jwtUtil.getAllClaimsFromToken(authHeader);

					// 5. Mutate Request to Pass Headers
					ServerHttpRequest request = exchange.getRequest().mutate()
							.header("loggedInUserRole", String.valueOf(claims.get("roles")))
							.header("loggedInUser", claims.getSubject()).build();

					// 6. Forward the Modified Request
					return chain.filter(exchange.mutate().request(request).build());

				} catch (Exception e) {
					System.out.println("Invalid Token Access!");
					throw new RuntimeException("Unauthorized Access: " + e.getMessage());
				}
			}

			// For non-secured routes, just continue
			return chain.filter(exchange);
		});
	}

	public static class Config {
		// Configuration properties
	}
}