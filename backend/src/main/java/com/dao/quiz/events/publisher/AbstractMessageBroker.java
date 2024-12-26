package com.dao.quiz.events.publisher;

import com.dao.quiz.events.SystemEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;

@Slf4j
public abstract class AbstractMessageBroker implements MessageBroker {
    protected final ObjectMapper mapper;
    protected final ApplicationEventPublisher eventPublisher;

    protected AbstractMessageBroker(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
        this.mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .registerModule(new ParameterNamesModule())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    protected static class EventWrapper {
        private String eventClass;
        private String data;
    }

    protected <T extends SystemEvent> EventWrapper wrapEvent(T event) {
        try {
            return new EventWrapper(
                    event.getClass().getName(),
                    mapper.writeValueAsString(event)
            );
        } catch (Exception e) {
            log.error("Failed to wrap event", e);
            throw new RuntimeException("Failed to wrap event", e);
        }
    }

    protected SystemEvent unwrapEvent(EventWrapper wrapper) {
        try {
            Class<?> eventClass = Class.forName(wrapper.getEventClass());
            return (SystemEvent) mapper.readValue(wrapper.getData(), eventClass);
        } catch (Exception e) {
            log.error("Failed to unwrap event", e);
            throw new RuntimeException("Failed to unwrap event", e);
        }
    }

    protected SystemEvent parseEvent(EventWrapper wrapper) throws Exception {
        try {
            log.debug("Parsing event. Class: {}", wrapper.getEventClass());
            Class<?> eventClass = Class.forName(wrapper.getEventClass());
            if (!SystemEvent.class.isAssignableFrom(eventClass)) {
                throw new IllegalArgumentException("Event class must be a subclass of SystemEvent");
            }
            // Parse event data
            SystemEvent event = (SystemEvent) mapper.readValue(wrapper.getData(), eventClass);
            log.debug("Successfully parsed event: {}", event);
            return event;
        } catch (Exception e) {
            log.error("Unable to parse system event: {}", e.getMessage());
            throw e;
        }
    }

    protected void publishToSpringContext(SystemEvent event) {
        try {
            log.debug("Publishing event to Spring context: {}", event);
            eventPublisher.publishEvent(event);
        } catch (Exception e) {
            log.error("Failed to publish event to Spring context", e);
        }
    }

}
