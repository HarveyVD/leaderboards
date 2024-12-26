package com.dao.quiz.exceptions;

public class NotAccessTokenException extends RuntimeException {
    public NotAccessTokenException(String messasge) {
        super(messasge);
    }
}
