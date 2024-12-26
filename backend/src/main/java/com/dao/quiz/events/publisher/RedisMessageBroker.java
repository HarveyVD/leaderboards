package com.dao.quiz.events.publisher;

import com.dao.quiz.events.SystemEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.lettuce.core.RedisBusyException;
import jakarta.annotation.PostConstruct;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.Subscription;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.HashMap;

@Component
@ConditionalOnProperty(name = "application.messaging.message-broker", havingValue = "redis")
@Slf4j
public class RedisMessageBroker extends AbstractMessageBroker implements StreamListener<String, MapRecord<String, String, String>> {
    private static final String STREAM_NAME = "application-events";
    private static final String CONSUMER_GROUP = "application-events-group";
    private static final String CONSUMER_NAME = "application-events-consumer";
    private static final String EVENT_DATA_FIELD = "data";
    private StreamMessageListenerContainer<String, MapRecord<String, String, String>> streamContainer;
    private Subscription subscription;
    private final RedisTemplate<String, String> redisTemplate;
    private final RedisConnectionFactory connectionFactory;
    private StreamMessageListenerContainer<String, MapRecord<String, String, String>> container;
    public RedisMessageBroker(ApplicationEventPublisher eventPublisher,
                              RedisTemplate<String, String> redisTemplate,
                              RedisConnectionFactory connectionFactory) {
        super(eventPublisher);
        this.redisTemplate = redisTemplate;
        this.connectionFactory = connectionFactory;
    }

    @PostConstruct
    public void init() {
        try {
            log.info("Trying to create stream for system events...");
            redisTemplate.opsForStream().createGroup(STREAM_NAME, CONSUMER_GROUP);
        } catch (RedisSystemException e) {
            if (e.getRootCause() instanceof RedisBusyException) {
                log.info("Consumer group already exists: {}", CONSUMER_GROUP);
            } else {
                throw e;
            }
        }
        var options = StreamMessageListenerContainer
                .StreamMessageListenerContainerOptions
                .builder()
                .batchSize(1)
                .pollTimeout(Duration.ofMillis(500))
                .build();

        log.info("Creating stream listener container...");
        this.streamContainer = StreamMessageListenerContainer.create(connectionFactory, options);

        log.info("Creating stream subscription...");
        this.subscription = streamContainer.receive(
                Consumer.from(CONSUMER_GROUP, CONSUMER_NAME),
                StreamOffset.create(STREAM_NAME, ReadOffset.lastConsumed()),
                this);

        streamContainer.start();
        log.info("Started stream listener container. Container started: '{}'. Current subscription active: '{}'",
                streamContainer.isRunning(), subscription.isActive());
    }

    @Override
    public <T extends SystemEvent> void sendEventToQueue(T event) {
        try {
            EventWrapper wrapper = wrapEvent(event);
            String data = mapper.writeValueAsString(wrapper);
            log.debug("Checking if event '{}' message can be converted back to event object...", event.getClass());
            parseEvent(mapper.readValue(data, EventWrapper.class));
            HashMap<String, String> fields = new HashMap<>();
            fields.put(EVENT_DATA_FIELD, data);

            StringRecord record = StreamRecords.string(fields)
                    .withStreamKey(STREAM_NAME);

            redisTemplate.opsForStream().add(record);
            log.debug("Sent event to Redis stream: {}", event);
        } catch (Exception e) {
            log.error("Failed to send event to Redis", e);
            // Fallback to direct Spring event publishing
            publishToSpringContext(event);
        }
    }

    @Override
    public void onMessage(MapRecord<String, String, String> message) {
        try {
            String data = message.getValue().get(EVENT_DATA_FIELD);
            EventWrapper wrapper = mapper.readValue(data, EventWrapper.class);
            SystemEvent event = unwrapEvent(wrapper);
            publishToSpringContext(event);
        } catch (Exception e) {
            log.error("Failed to process Redis message", e);
        }
    }

    @Override
    public void start() {
        var options = StreamMessageListenerContainer
                .StreamMessageListenerContainerOptions
                .builder()
                .pollTimeout(Duration.ofMillis(100))
                .build();

        container = StreamMessageListenerContainer.create(connectionFactory, options);
        container.receive(
                Consumer.from(CONSUMER_GROUP, CONSUMER_NAME),
                StreamOffset.create(STREAM_NAME, ReadOffset.lastConsumed()),
                this);
        container.start();
    }

    @Override
    public void stop() {
        if (container != null) {
            container.stop();
        }
    }
}
