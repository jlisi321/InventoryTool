package com.example.inventory_application.exception;

public class IllegalStateTransitionException extends RuntimeException {
    public IllegalStateTransitionException(String message) {
        super(message);
    }
}
