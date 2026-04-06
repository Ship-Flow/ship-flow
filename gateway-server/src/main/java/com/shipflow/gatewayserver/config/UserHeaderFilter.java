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

import com.shipflow.gatewayserver.exception.BusinessException;
import com.shipflow.gatewayserver.exception.GateErrorCode;

@Component
public class UserHeaderFilter implements GlobalFilter {

    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String USER_ROLE_HEADER = "X-User-Role";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        // 모든 요청에서 먼저 헤더 제거 (핵심!)
        ServerWebExchange sanitizedExchange = exchange.mutate()
            .request(exchange.getRequest().mutate()
                .headers(headers -> {
                    headers.remove(USER_ID_HEADER);
                    headers.remove(USER_ROLE_HEADER);
                })
                .build())
            .build();

        return sanitizedExchange.getPrincipal()
            .ofType(Authentication.class)
            .flatMap(auth -> {

                if (auth instanceof JwtAuthenticationToken jwtAuth) {
                    Jwt jwt = jwtAuth.getToken();

                    String userId = jwt.getSubject();
                    String role = extractRole(jwt);

                    if (userId == null || userId.isBlank() || role == null || role.isBlank()) {
                        return Mono.error(new BusinessException(GateErrorCode.MISSING_ROLES));
                    }

                    // JWT 값으로만 재주입
                    ServerHttpRequest mutated = sanitizedExchange.getRequest().mutate()
                        .headers(headers -> {
                            headers.add(USER_ID_HEADER, userId);
                            headers.add(USER_ROLE_HEADER, role);
                        })
                        .build();

                    return chain.filter(
                        sanitizedExchange.mutate().request(mutated).build()
                    );
                }

                return chain.filter(sanitizedExchange);
            })
            .switchIfEmpty(chain.filter(sanitizedExchange));
    }


    private String extractRole(Jwt jwt) { //role 추출
        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
        if (realmAccess == null) {
            throw new BusinessException(GateErrorCode.MISSING_REALM_ACCESS);
        }

        Object rolesObj = realmAccess.get("roles");
        if (!(rolesObj instanceof List<?> roles) || roles.isEmpty()) {
            throw new BusinessException(GateErrorCode.MISSING_ROLES);
        }
        return roles.get(0).toString();
    }
}