package com.auth.exception;

import com.auth.enums.ErrorCode;
import lombok.Getter;

@Getter
public class UserAlreadyExistsException extends RuntimeException {

    private final ErrorCode errorCode;

    public UserAlreadyExistsException(String message) {
        super(message);
        this.errorCode = ErrorCode.USER_ALREADY_EXISTS;
    }
}
