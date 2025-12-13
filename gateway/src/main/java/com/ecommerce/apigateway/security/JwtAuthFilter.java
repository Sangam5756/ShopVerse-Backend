package com.ecommerce.apigateway.security;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
@Component
public class JwtAuthFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;

    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /* ================= PUBLIC ================= */
    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/auth",
            "/api/products",
            "/api/categories"
    );

    /* ================= RBAC ================= */
    private static final Map<String, List<String>> ROLE_RULES = Map.of(
            "/api/products", List.of("ADMIN"),
            "/api/categories", List.of("ADMIN"),
            "/api/users", List.of("ADMIN", "CUSTOMER")
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();
        HttpMethod method = exchange.getRequest().getMethod();

        // 🌍 PUBLIC (GET only)
        if (isPublic(path, method)) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return unauthorized(exchange);
        }

        String token = authHeader.substring(7);

        try {
            String email = jwtUtil.getEmail(token);
            String role = jwtUtil.getRole(token);

            // 🛡 RBAC
            if (!isAuthorized(path, role)) {
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }

            // ✅ Forward trusted headers
            ServerWebExchange mutated = exchange.mutate()
                    .request(r -> r
                            .header("X-User-Email", email)
                            .header("X-User-Role", role)
                    )
                    .build();

            return chain.filter(mutated);

        } catch (Exception e) {
            return unauthorized(exchange);
        }
    }

    /* ================= HELPERS ================= */

    private boolean isPublic(String path, HttpMethod method) {
        return method == HttpMethod.GET &&
                PUBLIC_PATHS.stream().anyMatch(path::startsWith);
    }

    private boolean isAuthorized(String path, String role) {
        return ROLE_RULES.entrySet().stream()
                .filter(e -> path.startsWith(e.getKey()))
                .findFirst()
                .map(e -> e.getValue().contains(role))
                .orElse(true);
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return -1;
    }
}

