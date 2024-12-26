package com.dao.quiz.notifications.processors;

import com.dao.quiz.dto.ApiResponse;
import com.dao.quiz.models.domain.User;
import com.dao.quiz.notifications.NotificationProcessor;
import com.dao.quiz.notifications.model.LeaderboardScoreUpdatedNotification;
import com.dao.quiz.notifications.model.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import static com.dao.quiz.constants.MessagingConstants.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class WsNotificationProcessor implements NotificationProcessor {
    private final SimpMessagingTemplate simpMessagingTemplate;
    @Override
    public void process(User user, Notification notification) {
        switch (notification.getSourceType()) {
            case LEADERBOARD:
                String quizId = String.valueOf(notification.getTarget().getQuizId());
                String destination = String.format("%s/%s%s",QUEUE_LEADERBOARD, quizId, NOTIFICATIONS_SUFFIX);
                sendTo(destination, ApiResponse.success(((LeaderboardScoreUpdatedNotification) notification).getLeaderboards()));
                break;
            default:
                // SKIPPING
                log.warn("Skipping sending of {} notification because source type is not specified", notification.getNotificationType());
        }
    }

    private void sendToUser(String username, String destination, Object payload) {
        try {
            if (username == null) {
                return;
            }
            simpMessagingTemplate.convertAndSendToUser(username, destination, payload);
        } catch (MessageDeliveryException ex) {
            log.error("Unable to send ws message to user.", ex);
        }
    }

    private void sendTo(String destination, Object payload) {
        try {
            simpMessagingTemplate.convertAndSend(destination, payload);
        } catch (MessageDeliveryException ex) {
            log.error("Unable to send ws message to user.", ex);
        }
    }

}
