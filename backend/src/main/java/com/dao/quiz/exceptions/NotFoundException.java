package com.dao.quiz.exceptions;

import org.springframework.http.HttpStatus;

public class NotFoundException extends WebException {
    public NotFoundException(ErrorCode errorCode, String message) {
        super(HttpStatus.NOT_FOUND, errorCode, message);
    }
}
