package com.dao.quiz.events;

import org.springframework.context.ApplicationEvent;

public abstract class SystemEvent extends ApplicationEvent {
    protected SystemEvent(Object source) {
        super(source);
    }
}
