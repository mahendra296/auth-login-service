package com.auth.exception;

import com.auth.enums.ErrorCode;
import lombok.Getter;

@Getter
public class OAuthException extends RuntimeException {

    private final ErrorCode errorCode;

    public OAuthException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public OAuthException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}
