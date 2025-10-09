package com.amdocs.schedulease.exception;

public class RoomNotFoundException extends RuntimeException {
    
    public RoomNotFoundException(String message) {
        super(message);
    }
    
    public RoomNotFoundException(Long roomId) {
        super("Room not found with ID: " + roomId);
    }
    
    public RoomNotFoundException(String field, String value) {
        super("Room not found with " + field + ": " + value);
    }
}
