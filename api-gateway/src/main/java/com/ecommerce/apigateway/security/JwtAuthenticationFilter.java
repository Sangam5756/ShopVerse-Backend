package com.ecommerce.apigateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

	@Value("${jwt.secret}")
	private String secret;

	private final List<String> openEndpoints = List.of(
			"/api/auth/login",
			"/api/auth/register",
			"/api/auth/refresh-token",
			"/eureka",
			"/actuator/health"
	);

	public JwtAuthenticationFilter() {
		super(Config.class);
	}

	@Override
	public GatewayFilter apply(Config config) {
		return (exchange, chain) -> {
			ServerHttpRequest request = exchange.getRequest();
			String path = request.getPath().toString();

			// Skip JWT check for open endpoints
			if (isOpenEndpoint(path)) {
				return chain.filter(exchange);
			}

			// Get the JWT token from the Authorization header
			String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

			if (authHeader == null || !authHeader.startsWith("Bearer ")) {
				return onError(exchange, "No authorization header", HttpStatus.UNAUTHORIZED);
			}

			String token = authHeader.substring(7);

			try {
				// Validate JWT token
				if (!isJwtValid(token)) {
					return onError(exchange, "Invalid token", HttpStatus.UNAUTHORIZED);
				}

				// Extract claims from JWT
				Claims claims = getJwtClaims(token);
				String userId = claims.getSubject();
				String email = claims.get("sub", String.class); // Get email from 'sub' claim

				// Add user details to the request headers
				ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
						.header("X-User-Id", userId)
						.header("X-User-Email", email) // Add email header
						.build();

				System.out.println(modifiedRequest);

				return chain.filter(exchange.mutate().request(modifiedRequest).build());
			} catch (Exception e) {
				return onError(exchange, "Invalid token: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
			}
		};
	}

	private boolean isOpenEndpoint(String path) {
		return openEndpoints.stream().anyMatch(path::startsWith);
	}

	private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
		ServerHttpResponse response = exchange.getResponse();
		response.setStatusCode(httpStatus);
		response.getHeaders().add(HttpHeaders.CONTENT_TYPE, "application/json");
		String errorMessage = "{\"error\": \"" + err + "\"}";
		DataBuffer buffer = response.bufferFactory().wrap(errorMessage.getBytes(StandardCharsets.UTF_8));
		return response.writeWith(Mono.just(buffer));
	}

	private String getAuthHeader(ServerHttpRequest request) {
		return request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
	}

	private boolean isAuthMissing(ServerHttpRequest request) {
		return getAuthHeader(request) == null || !getAuthHeader(request).startsWith("Bearer ");
	}

	private boolean isJwtValid(String token) {
		try {
			getJwtClaims(token);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private Claims getJwtClaims(String token) {
		return Jwts.parserBuilder()
				.setSigningKey(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
				.build()
				.parseClaimsJws(token)
				.getBody();
	}

	public static class Config {
		// Configuration properties if needed
	}
}