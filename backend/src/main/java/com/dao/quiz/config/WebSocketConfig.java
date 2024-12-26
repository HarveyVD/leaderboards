package com.dao.quiz.config;

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

import static com.dao.quiz.constants.MessagingConstants.*;
import static com.dao.quiz.constants.WebConstants.API_WEBSOCKET_PREFIX;

@Configuration
@EnableWebSocketMessageBroker
@Slf4j
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final ActiveMqBrokerConfigurationProps activeMqBrokerConfigurationProps;
    private final MeterRegistry meterRegistry;
    private final AtomicInteger activeSessions = new AtomicInteger(0);

    @PostConstruct
    public void init() {
        meterRegistry.gauge("websocket.sessions.active", 
            Tags.of("pod", System.getenv("HOSTNAME")), activeSessions);
    }

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
        registration.addDecoratorFactory(handler -> {
            return new WebSocketHandlerDecorator(handler) {
                @Override
                public void afterConnectionEstablished(WebSocketSession session) throws Exception {
                    meterRegistry.counter("websocket.sessions.total").increment();
                    activeSessions.incrementAndGet();
                    super.afterConnectionEstablished(session);
                }

                @Override
                public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
                    activeSessions.decrementAndGet();
                    super.afterConnectionClosed(session, closeStatus);
                }

                @Override
                public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
                    meterRegistry.counter("websocket.messages.received",
                        Tags.of("pod", System.getenv("HOSTNAME"))).increment();
                    super.handleMessage(session, message);
                }
            };
        });
    }
}
