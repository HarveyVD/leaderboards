package com.dao.quiz.services;

import com.dao.quiz.models.domain.Quiz;

import java.util.List;
import java.util.Optional;

public interface QuizzesManagementService {
    List<Quiz> getAllQuizzes();

    Optional<Quiz> getQuiz(Long quizId);
}
