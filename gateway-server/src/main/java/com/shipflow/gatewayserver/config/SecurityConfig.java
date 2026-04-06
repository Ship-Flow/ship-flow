package com.shipflow.gatewayserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import static org.springframework.http.HttpMethod.*;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private static final String[] WHITELIST = {
            "/api/auth/login",
            "/eureka/**",
            "/api/auth/signup-requests/**"
    };

    public static final String USERS = "/api/users/**";
    public static final String SIGNUP = "/api/auth/signup-requests/**";
    public static final String COMPANYS = "/api/companies/**";
    public static final String HUBS = "/api/hubs/**";
    public static final String HUBSROUTES = "/api/hubs-routes/**";
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
                        .jwt(Customizer.withDefaults())
                )

                .build();
    }
}