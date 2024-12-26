package com.dao.quiz.controllers.auth;

import com.dao.quiz.constants.WebConstants;
import com.dao.quiz.dto.auth.request.AuthRequest;
import com.dao.quiz.dto.auth.response.AuthResponse;
import com.dao.quiz.models.domain.User;
import com.dao.quiz.repositories.UserRepository;
import com.dao.quiz.security.providers.JWTTokenProvider;
import com.dao.quiz.utils.ProjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(WebConstants.API_V1_BASE_PREFIX + "/auth")
public class LoginController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JWTTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest authRequest) {
        User user = userRepository.findByUsername(authRequest.getUsername())
                .orElseGet(() -> {
                    // Create new user when it is not existed
                    User newUser = new User();
                    newUser.setUsername(authRequest.getUsername());
                    return userRepository.save(newUser);
                });

        String token = jwtTokenProvider.generateAccessToken(user, ProjectUtils.randomString(10));
        return new AuthResponse(token, user);
    }
}
