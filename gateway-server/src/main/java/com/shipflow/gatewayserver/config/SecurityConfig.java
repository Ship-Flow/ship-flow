package com.shipflow.gatewayserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.http.HttpMethod.*;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {

    private static final String[] WHITELIST = {
            "/api/auth/login",
            "/eureka/**"
    };

    public static final String USERS = "/api/users/**";
    public static final String SIGNUP = "/api/auth/signup-requests/**";
    public static final String COMPANYS = "/api/companies/**";
    public static final String HUBS = "/api/hubs/**";
    public static final String HUBSROUTES = "/api/hub-routes/**";
    public static final String PRODUCTS = "/api/companies/*/products/**";
    public static final String ORDERS = "/api/orders/**";
    public static final String SHIPMENT = "/api/shipments/**";
    public static final String SHIPMANAGER = "/api/shipment-managers/**";
    public static final String AILOG = "/api/ai/**";
    public static final String SLACK = "/api/slack/**";

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)

                .authorizeExchange(ex -> ex
                        .pathMatchers(WHITELIST).permitAll()

                        .pathMatchers(POST, SIGNUP).permitAll()
                        .pathMatchers(PATCH, SIGNUP).hasAnyRole("MASTER", "HUB_MANAGER")
                        .pathMatchers(GET, SIGNUP).hasAnyRole("MASTER", "HUB_MANAGER")

                        .pathMatchers(POST, COMPANYS).hasAnyRole("MASTER", "HUB_MANAGER")
                        .pathMatchers(DELETE, COMPANYS).hasAnyRole("MASTER", "HUB_MANAGER")

                        .pathMatchers(PATCH, ORDERS).hasAnyRole("MASTER", "HUB_MANAGER")
                        .pathMatchers(DELETE, ORDERS).hasAnyRole("MASTER", "HUB_MANAGER")

                        .pathMatchers(POST, PRODUCTS).hasAnyRole("MASTER", "HUB_MANAGER", "COMPANY_MANAGER")
                        .pathMatchers(PUT, PRODUCTS).hasAnyRole("MASTER", "HUB_MANAGER", "COMPANY_MANAGER")
                        .pathMatchers(PATCH, PRODUCTS).hasAnyRole("MASTER", "HUB_MANAGER", "COMPANY_MANAGER")
                        .pathMatchers(DELETE, PRODUCTS).hasAnyRole("MASTER", "HUB_MANAGER", "COMPANY_MANAGER")

                        .pathMatchers(PATCH, SHIPMANAGER).hasAnyRole("MASTER", "HUB_MANAGER")
                        .pathMatchers(DELETE, SHIPMANAGER).hasAnyRole("MASTER", "HUB_MANAGER")

                        .pathMatchers(GET, AILOG).hasRole("MASTER")

                        .pathMatchers(GET, SLACK).hasRole("MASTER")
                        .pathMatchers(PATCH, SLACK).hasRole("MASTER")
                        .pathMatchers(DELETE, SLACK).hasRole("MASTER")

                        .pathMatchers(POST, HUBS).hasRole("MASTER")
                        .pathMatchers(PATCH, HUBS).hasRole("MASTER")
                        .pathMatchers(DELETE, HUBS).hasRole("MASTER")

                        .pathMatchers(POST, HUBSROUTES).hasRole("MASTER")
                        .pathMatchers(PATCH, HUBSROUTES).hasRole("MASTER")
                        .pathMatchers(DELETE, HUBSROUTES).hasRole("MASTER")

                        .pathMatchers(DELETE, SHIPMENT).hasRole("MASTER")

                        .anyExchange().authenticated()
                )

                .oauth2ResourceServer(oauth -> oauth
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(grantedAuthoritiesExtractor()))
                )

                .build();
    }

    @Bean
    public Converter<Jwt, Mono<AbstractAuthenticationToken>> grantedAuthoritiesExtractor() {
        Converter<Jwt, Collection<GrantedAuthority>> delegate = jwt -> {
            JwtGrantedAuthoritiesConverter defaultConverter = new JwtGrantedAuthoritiesConverter();
            Collection<GrantedAuthority> defaultAuthorities = defaultConverter.convert(jwt);

            Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
            List<GrantedAuthority> realmRoles = List.of();

            if (realmAccess != null && realmAccess.get("roles") instanceof List<?> roles) {
                realmRoles = roles.stream()
                        .map(Object::toString)
                        .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
            }

            return Stream.concat(defaultAuthorities.stream(), realmRoles.stream())
                    .collect(Collectors.toSet());
        };

        return new ReactiveJwtAuthenticationConverterAdapter(jwt -> {
            Collection<GrantedAuthority> authorities = delegate.convert(jwt);
            return new JwtAuthenticationToken(jwt, authorities, jwt.getSubject());
        });
    }
}