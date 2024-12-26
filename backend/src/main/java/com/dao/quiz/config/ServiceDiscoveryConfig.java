package com.dao.quiz.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableDiscoveryClient
@ConditionalOnProperty(
        prefix = "application",
        name = "eureka.enabled",
        havingValue = "true",
        matchIfMissing = true  // Enable by default except when explicitly set to false
)
public class ServiceDiscoveryConfig {
}
