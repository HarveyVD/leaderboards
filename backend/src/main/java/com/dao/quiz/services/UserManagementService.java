package com.dao.quiz.services;

import com.dao.quiz.models.domain.User;

import java.util.Optional;

public interface UserManagementService {
    Optional<User> findUserById(Long userId);
}
