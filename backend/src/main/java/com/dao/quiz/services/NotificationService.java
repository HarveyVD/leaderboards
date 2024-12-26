package com.dao.quiz.services;

import com.dao.quiz.notifications.model.Notification;

public interface NotificationService {
    void processNotification(Notification notification);
}
