package com.example.inventory_application.dto;

import com.example.inventory_application.model.DispositionType;
import com.example.inventory_application.model.DispositionStatus;

import java.time.Instant;

public class DispositionRequestDTO {

    private Long id;
    private DispositionType type;
    private Integer quantity;
    private String justification;
    private DispositionStatus status;
    private Instant createdAt;
    private Instant updatedAt;

    public DispositionRequestDTO(Long id, DispositionType type, Integer quantity,
                                 String justification, DispositionStatus status,
                                 Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.type = type;
        this.quantity = quantity;
        this.justification = justification;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public DispositionType getType() {
        return type;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public String getJustification() {
        return justification;
    }

    public DispositionStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}

