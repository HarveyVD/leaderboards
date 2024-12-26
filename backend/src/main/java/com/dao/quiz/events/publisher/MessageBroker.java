package com.dao.quiz.events.publisher;

import com.dao.quiz.events.SystemEvent;

public interface MessageBroker {
    <T extends SystemEvent> void sendEventToQueue(T event);
    void start();
    void stop();
}
