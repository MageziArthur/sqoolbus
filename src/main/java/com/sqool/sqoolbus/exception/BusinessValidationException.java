package com.sqool.sqoolbus.exception;

/**
 * Exception thrown when a business rule validation fails
 */
public class BusinessValidationException extends SqoolbusException {

    public BusinessValidationException(String message) {
        super("BUSINESS_VALIDATION_ERROR", message);
    }

    public BusinessValidationException(String message, Throwable cause) {
        super("BUSINESS_VALIDATION_ERROR", message, cause);
    }
}