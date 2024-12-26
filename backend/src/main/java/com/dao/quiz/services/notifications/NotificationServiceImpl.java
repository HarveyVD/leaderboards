package com.dao.quiz.services.notifications;

import com.dao.quiz.notifications.NotificationProcessor;
import com.dao.quiz.notifications.model.Notification;
import com.dao.quiz.services.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationProcessor notificationProcessor;
    @Override
    public void processNotification(Notification notification) {
        log.info("Processing notification {}", notification.getNotificationType());
        switch (notification.getTarget().getNotificationType()) {
            case LEADERBOARD_SCORE_UPDATED:
                notificationProcessor.process(notification.getTarget().getUser(), notification);
                break;
            default:
                throw new IllegalArgumentException("Unsupported notification target " + notification.getTarget().getNotificationType());
        }
    }
}
