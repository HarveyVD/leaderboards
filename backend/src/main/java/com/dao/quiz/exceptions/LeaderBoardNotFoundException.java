package com.dao.quiz.exceptions;

public class LeaderBoardNotFoundException extends NotFoundException {
    public LeaderBoardNotFoundException(Long userId, Long quizId) {
        super(ErrorCode.LEADERBOARD_NOT_FOUND, String.format("Leaderboard not found for userId: %d and quizId: %d", userId, quizId));
    }
}
