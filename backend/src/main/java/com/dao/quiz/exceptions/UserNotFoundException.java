package com.dao.quiz.exceptions;

public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException() {
        super(ErrorCode.USER_NOT_FOUND, "User not found");
    }
}
