package com.example.inventory_application.exception;

public class PartNotFoundException extends RuntimeException {
    public PartNotFoundException(String partNumber) {
        super("Part not found: " + partNumber);
    }
}
