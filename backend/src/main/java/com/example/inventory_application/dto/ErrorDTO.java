package com.example.inventory_application.dto;

import java.time.Instant;

public class ErrorDTO {
    private Instant timestamp;
    private int status;
    private String error;
    private String message;

    public ErrorDTO(int status, String error, String message) {
        this.timestamp = Instant.now();
        this.status = status;
        this.error = error;
        this.message = message;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }
}
