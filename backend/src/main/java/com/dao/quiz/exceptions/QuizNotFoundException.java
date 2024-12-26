package com.dao.quiz.exceptions;

public class QuizNotFoundException extends NotFoundException {
    public QuizNotFoundException() {
        super(ErrorCode.QUIZ_NOT_FOUND, "Quiz not found");
    }
}
