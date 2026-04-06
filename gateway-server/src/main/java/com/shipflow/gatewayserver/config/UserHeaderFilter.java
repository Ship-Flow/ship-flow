package com.shipflow.gatewayserver.config;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Component
public class UserHeaderFilter implements GlobalFilter {

    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String USER_ROLE_HEADER = "X-User-Role";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return exchange.getPrincipal()
                .cast(Authentication.class)
                .flatMap(auth -> {
                    if (auth instanceof JwtAuthenticationToken jwtAuth) {
                        Jwt jwt = jwtAuth.getToken();

                        String userId = jwt.getSubject();
                        String role = extractRole(jwt);

                        ServerHttpRequest mutated = exchange.getRequest().mutate()
                                .headers(headers -> {
                                    headers.remove(USER_ID_HEADER); //유입된 위조 가능성 헤더 제거
                                    headers.remove(USER_ROLE_HEADER);
                                    headers.add(USER_ID_HEADER, userId);
                                    headers.add(USER_ROLE_HEADER, role);
                                })
                                .build();

                        return chain.filter(exchange.mutate().request(mutated).build());
                    }

                    return chain.filter(exchange);
                })
                .switchIfEmpty(chain.filter(exchange));
    }

    private String extractRole(Jwt jwt) { //role 추출
        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
        if (realmAccess == null) {
            throw new IllegalArgumentException("realm_access claim is missing");
        }

        Object rolesObj = realmAccess.get("roles");
        if (!(rolesObj instanceof List<?> roles) || roles.isEmpty()) {
            throw new IllegalArgumentException("roles claim is missing or empty");
        }

        return roles.get(0).toString();
    }
}