package com.dao.quiz.events.scores;

import com.dao.quiz.events.SystemEvent;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UserScoreUpdatedEvent extends SystemEvent {
    private final Long userId;
    private final Long leaderBoardId;
    private final Long quizId;
    private final Integer score;

    @JsonCreator
    public UserScoreUpdatedEvent(@JsonProperty("user_id") Long userId,
                                 @JsonProperty("quiz_id") Long quizId,
                                 @JsonProperty("leader_board_id") Long leaderBoardId,
                                  @JsonProperty("score") Integer score) {
        super(userId);
        this.userId = userId;
        this.quizId = quizId;
        this.leaderBoardId = leaderBoardId;
        this.score = score;
    }
}
