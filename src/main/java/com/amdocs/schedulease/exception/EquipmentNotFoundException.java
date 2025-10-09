package com.amdocs.schedulease.exception;

public class EquipmentNotFoundException extends RuntimeException {
    
    public EquipmentNotFoundException(String message) {
        super(message);
    }
    
    public EquipmentNotFoundException(Long equipmentId) {
        super("Equipment not found with ID: " + equipmentId);
    }
    
    public EquipmentNotFoundException(String field, String value) {
        super("Equipment not found with " + field + ": " + value);
    }
}
