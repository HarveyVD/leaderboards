package com.dao.quiz.services.users;

import com.dao.quiz.models.domain.User;
import com.dao.quiz.repositories.UserRepository;
import com.dao.quiz.services.UserManagementService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserManagementServiceImpl implements UserManagementService {
    private final UserRepository userRepository;
    @Override
    public Optional<User> findUserById(Long userId) {
        return userRepository.findById(userId);
    }
}
