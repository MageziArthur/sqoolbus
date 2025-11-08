package com.sqool.sqoolbus.exception;

/**
 * Exception thrown when authentication fails
 */
public class AuthenticationException extends SqoolbusException {

    public AuthenticationException(String message) {
        super("AUTHENTICATION_ERROR", message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super("AUTHENTICATION_ERROR", message, cause);
    }
}