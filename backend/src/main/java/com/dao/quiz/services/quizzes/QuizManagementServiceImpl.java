package com.dao.quiz.services.quizzes;

import com.dao.quiz.models.domain.Quiz;
import com.dao.quiz.repositories.QuizRepository;
import com.dao.quiz.services.QuizzesManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuizManagementServiceImpl implements QuizzesManagementService {
    private final QuizRepository quizRepository;
    @Override
    public List<Quiz> getAllQuizzes() {
        return quizRepository.findAll();
    }

    @Override
    public Optional<Quiz> getQuiz(Long quizId) {
        return quizRepository.findById(quizId);
    }
}
