package com.dao.quiz.config;

import com.dao.quiz.config.handlers.WebSocketMetricsHandler;
import com.dao.quiz.config.props.ActiveMqBrokerConfigurationProps;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;

import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Optional;

import static com.dao.quiz.constants.MessagingConstants.*;
import static com.dao.quiz.constants.WebConstants.API_WEBSOCKET_PREFIX;

@Configuration
@EnableWebSocketMessageBroker
@Slf4j
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final ActiveMqBrokerConfigurationProps activeMqBrokerConfigurationProps;
    private final Optional<WebSocketMetricsHandler> metricsHandler;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(API_WEBSOCKET_PREFIX)
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(@NonNull MessageBrokerRegistry registry) {
        if (StringUtils.isNotBlank(activeMqBrokerConfigurationProps.getHost())) {
            registry.enableStompBrokerRelay(QUEUE_DEFAULT_NAME + "/", "/topic/")
                    .setRelayHost(activeMqBrokerConfigurationProps.getHost())
                    .setRelayPort(activeMqBrokerConfigurationProps.getPort())
                    .setUserDestinationBroadcast("/topic/registry.broadcast")
                    .setSystemLogin(activeMqBrokerConfigurationProps.getUsername())
                    .setSystemPasscode(activeMqBrokerConfigurationProps.getPassword())
                    .setClientLogin(activeMqBrokerConfigurationProps.getUsername())
                    .setClientPasscode(activeMqBrokerConfigurationProps.getPassword())
                    .setSystemHeartbeatSendInterval(15_000)
                    .setSystemHeartbeatReceiveInterval(15_000);
        } else {
            registry.enableSimpleBroker(QUEUE_DEFAULT_NAME + "/", "/topic/")
                    .setHeartbeatValue(new long[] {25000, 25000})
                    .setTaskScheduler(new ConcurrentTaskScheduler(Executors.newScheduledThreadPool(100)));
        }
        registry.setApplicationDestinationPrefixes(DESTINATION_APP_PREFIX);
        registry.setUserDestinationPrefix(DESTINATION_USER_PREFIX);
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        metricsHandler.ifPresent(handler -> handler.configureWebSocketTransport(registration));
    }
}
