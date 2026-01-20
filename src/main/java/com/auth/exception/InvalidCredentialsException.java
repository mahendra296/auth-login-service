package com.auth.exception;

import com.auth.enums.ErrorCode;
import lombok.Getter;

@Getter
public class InvalidCredentialsException extends RuntimeException {

    private final ErrorCode errorCode;

    public InvalidCredentialsException(String message) {
        super(message);
        this.errorCode = ErrorCode.INVALID_CREDENTIALS;
    }
}
