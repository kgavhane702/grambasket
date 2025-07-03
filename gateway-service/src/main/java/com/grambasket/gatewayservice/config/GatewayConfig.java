package com.grambasket.gatewayservice.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            .route("auth-service", r -> r
                .path("/grambasket/api/auth-service/**")
                .filters(f -> f.rewritePath("/grambasket/api/auth-service/(?<segment>.*)", "/api/auth-service/${segment}"))
                .uri("lb://auth-service"))
            .build();
    }
}