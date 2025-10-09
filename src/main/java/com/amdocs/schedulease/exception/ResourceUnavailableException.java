package com.amdocs.schedulease.exception;

public class ResourceUnavailableException extends RuntimeException {
    
    public ResourceUnavailableException(String message) {
        super(message);
    }
    
    public ResourceUnavailableException(String resourceType, Long resourceId) {
        super(resourceType + " with ID " + resourceId + " is currently unavailable");
    }
    
    public ResourceUnavailableException(String resourceType, String resourceName, String reason) {
        super(resourceType + " '" + resourceName + "' is unavailable. Reason: " + reason);
    }
}
