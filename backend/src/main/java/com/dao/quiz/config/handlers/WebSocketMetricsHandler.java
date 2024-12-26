package com.dao.quiz.config.handlers;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;

import java.util.concurrent.atomic.AtomicInteger;

@Component
@Profile("k8s")
@RequiredArgsConstructor
public class WebSocketMetricsHandler {
    private final MeterRegistry meterRegistry;

    @PostConstruct
    public void init() {
        meterRegistry.gauge("websocket.sessions.active", 
            Tags.of("pod", System.getenv("HOSTNAME")), activeSessions);
    }
    private AtomicInteger activeSessions = new AtomicInteger(0);

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
