package com.sqool.sqoolbus.exception;

/**
 * Exception thrown when a duplicate resource is detected
 */
public class DuplicateResourceException extends SqoolbusException {

    public DuplicateResourceException(String resourceType, String field, String value) {
        super("DUPLICATE_RESOURCE", String.format("%s with %s '%s' already exists", resourceType, field, value));
    }

    public DuplicateResourceException(String message) {
        super("DUPLICATE_RESOURCE", message);
    }
}