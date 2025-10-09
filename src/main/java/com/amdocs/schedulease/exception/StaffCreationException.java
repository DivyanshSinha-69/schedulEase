package com.amdocs.schedulease.exception;

public class StaffCreationException extends RuntimeException {
    public StaffCreationException(String message) {
        super(message);
    }
    
    public StaffCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
