package com.dao.quiz.security.services;

import com.dao.quiz.exceptions.UserNotFoundException;
import com.dao.quiz.models.domain.User;
import com.dao.quiz.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class AuthUserDetailService implements UserDetailsService {
    private final UserRepository userRepository;

    public AuthUserDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        return new AuthUserDetails(user);
    }
}
