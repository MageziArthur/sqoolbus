package com.sqool.sqoolbus.exception;

/**
 * Base exception class for all Sqoolbus application exceptions
 */
public abstract class SqoolbusException extends RuntimeException {

    private final String errorCode;

    protected SqoolbusException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    protected SqoolbusException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}