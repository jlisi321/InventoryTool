package com.example.inventory_application.exception;

public class ActiveRequestExistsException extends RuntimeException {
    public ActiveRequestExistsException(String partNumber) {
        super("Part " + partNumber + " already has an active disposition request");
    }
}
