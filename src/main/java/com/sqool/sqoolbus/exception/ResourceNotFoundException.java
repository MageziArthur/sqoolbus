package com.sqool.sqoolbus.exception;

/**
 * Exception thrown when a requested resource is not found
 */
public class ResourceNotFoundException extends SqoolbusException {

    public ResourceNotFoundException(String resourceType, String identifier) {
        super("RESOURCE_NOT_FOUND", String.format("%s not found with identifier: %s", resourceType, identifier));
    }

    public ResourceNotFoundException(String resourceType, String field, String value) {
        super("RESOURCE_NOT_FOUND", String.format("%s not found with %s: %s", resourceType, field, value));
    }

    public ResourceNotFoundException(String message) {
        super("RESOURCE_NOT_FOUND", message);
    }
}