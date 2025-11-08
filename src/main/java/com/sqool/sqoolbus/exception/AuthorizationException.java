package com.sqool.sqoolbus.exception;

/**
 * Exception thrown when a user tries to perform an unauthorized action
 */
public class AuthorizationException extends SqoolbusException {

    public AuthorizationException(String message) {
        super("AUTHORIZATION_ERROR", message);
    }

    public AuthorizationException(String message, Throwable cause) {
        super("AUTHORIZATION_ERROR", message, cause);
    }
}