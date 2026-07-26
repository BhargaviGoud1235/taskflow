package com.bhargavi.taskflow.exception;

public class AccessDeniedCustomException extends RuntimeException {
    public AccessDeniedCustomException(String message) {
        super(message);
    }
}
