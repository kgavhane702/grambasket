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
                // The pattern for each route is:
                // 1. path(): The public-facing URL the gateway will listen for.
                // 2. filters(): Rewrites the path to match the internal API path of the microservice.
                // 3. uri(): Forwards the request to the correct service using its name from Eureka (lb:// means load-balanced).

                .route("auth-service", r -> r
                        .path("/grambasket/api/auth-service/**")
                        .filters(f -> f.rewritePath("/grambasket/api/auth-service/(?<segment>.*)", "/api/auth-service/${segment}"))
                        .uri("lb://auth-service"))

                .route("user-service", r -> r
                        .path("/grambasket/api/user-service/**")
                        .filters(f -> f.rewritePath("/grambasket/api/user-service/(?<segment>.*)", "/api/user-service/${segment}"))
                        .uri("lb://user-service"))

                .route("product-service", r -> r
                        .path("/grambasket/api/product-service/**")
                        .filters(f -> f.rewritePath("/grambasket/api/product-service/(?<segment>.*)", "/api/product-service/${segment}"))
                        .uri("lb://product-service"))

                .route("inventory-service", r -> r
                        .path("/grambasket/api/inventory-service/**")
                        .filters(f -> f.rewritePath("/grambasket/api/inventory-service/(?<segment>.*)", "/api/inventory-service/${segment}"))
                        .uri("lb://inventory-service"))

                .route("cart-service", r -> r
                        .path("/grambasket/api/cart-service/**")
                        .filters(f -> f.rewritePath("/grambasket/api/cart-service/(?<segment>.*)", "/api/cart-service/${segment}"))
                        .uri("lb://cart-service"))

                .route("order-service", r -> r
                        .path("/grambasket/api/order-service/**")
                        .filters(f -> f.rewritePath("/grambasket/api/order-service/(?<segment>.*)", "/api/order-service/${segment}"))
                        .uri("lb://order-service"))

                .route("payment-service", r -> r
                        .path("/grambasket/api/payment-service/**")
                        .filters(f -> f.rewritePath("/grambasket/api/payment-service/(?<segment>.*)", "/api/payment-service/${segment}"))
                        .uri("lb://payment-service"))

                .route("notification-service", r -> r
                        .path("/grambasket/api/notification-service/**")
                        .filters(f -> f.rewritePath("/grambasket/api/notification-service/(?<segment>.*)", "/api/notification-service/${segment}"))
                        .uri("lb://notification-service"))

                .route("delivery-service", r -> r
                        .path("/grambasket/api/delivery-service/**")
                        .filters(f -> f.rewritePath("/grambasket/api/delivery-service/(?<segment>.*)", "/api/delivery-service/${segment}"))
                        .uri("lb://delivery-service"))

                .route("review-rating-service", r -> r
                        .path("/grambasket/api/review-rating-service/**")
                        .filters(f -> f.rewritePath("/grambasket/api/review-rating-service/(?<segment>.*)", "/api/review-rating-service/${segment}"))
                        .uri("lb://review-rating-service"))

                .build();
    }
}