package com.dao.gateway.config;


import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
public class RouteLocatorConfig {
    @Bean
    public RouteLocator myRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
            .route(p -> p
                .path("/websocket/**")
                .filters(f -> f
                        .dedupeResponseHeader(
                                "Access-Control-Allow-Credentials Access-Control-Allow-Origin",
                                "RETAIN_FIRST"
                        )
                )
                .uri("lb:ws://quiz/api/v1/ws/websocket")
            )
            .route(p -> p
                .path("/sockjs-websocket/**")
                .filters(f -> f
                        .dedupeResponseHeader(
                                "Access-Control-Allow-Credentials Access-Control-Allow-Origin",
                                "RETAIN_FIRST"
                        )
                )
                .uri("lb://quiz/api/v1/ws/sockjs-websocket")
            )
            // Route API v1
            .route(p -> p
                .path("/api/v1/**")
                .filters(f -> f
                        .dedupeResponseHeader(
                                "Access-Control-Allow-Credentials Access-Control-Allow-Origin",
                                "RETAIN_FIRST"
                        )
                )
                .uri("lb://quiz/api/v1")
            )
            .build();
    }
}
