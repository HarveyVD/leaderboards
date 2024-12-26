package com.dao.quiz.config;

import com.dao.quiz.exceptions.ErrorCode;
import com.dao.quiz.exceptions.WebException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;

@Slf4j
public class AuthFailureHandler implements org.springframework.security.web.authentication.AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) {
        if (exception.getCause() instanceof WebException) {
            throw (RuntimeException)exception.getCause();
        } else {
            log.error("Login failed because of exception", exception);
            throw WebException.unauthorized(ErrorCode.BAD_CREDENTIALS, "unable to login");
        }
    }
}
