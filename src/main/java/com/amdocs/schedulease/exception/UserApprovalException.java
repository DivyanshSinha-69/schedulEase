package com.amdocs.schedulease.exception;

public class UserApprovalException extends RuntimeException {
    public UserApprovalException(String message) {
        super(message);
    }
    
    public UserApprovalException(String message, Throwable cause) {
        super(message, cause);
    }
}
