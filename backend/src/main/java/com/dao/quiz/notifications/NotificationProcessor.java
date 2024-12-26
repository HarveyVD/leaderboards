package com.dao.quiz.notifications;

import com.dao.quiz.models.domain.User;
import com.dao.quiz.notifications.model.Notification;

@FunctionalInterface
public interface NotificationProcessor {
    void process(User user, Notification notification);
}
