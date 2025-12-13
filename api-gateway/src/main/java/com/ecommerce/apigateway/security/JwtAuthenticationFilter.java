package com.ecommerce.apigateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Predicate;

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

				// Add user details to the request headers
				String userId = getUserIdFromJwt(token);
				ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
						.header("X-User-Id", userId)
						.build();

				return chain.filter(exchange.mutate().request(modifiedRequest).build());
			} catch (Exception e) {
				return onError(exchange, "Invalid token", HttpStatus.UNAUTHORIZED);
			}
		};
	}

	private boolean isOpenEndpoint(String path) {
		return openEndpoints.stream().anyMatch(path::startsWith);
	}

	private Mono<Void> onError(ServerWebExchange exchange, String error, HttpStatus httpStatus) {
		exchange.getResponse().setStatusCode(httpStatus);
		return exchange.getResponse().setComplete();
	}

	private boolean isJwtValid(String token) {
		try {
			getJwtClaims(token);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private String getUserIdFromJwt(String token) {
		return getJwtClaims(token).getSubject();
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