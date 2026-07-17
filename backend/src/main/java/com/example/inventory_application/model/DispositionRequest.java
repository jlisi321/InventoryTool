package com.example.inventory_application.model;

import java.time.Instant;

public class DispositionRequest {

    // Used Long instead of long because id will be null until this request is successfully inserted into the DB and it's generated
    private Long id;
    private String partNumber;
    private DispositionType type;
    // Used Integer instead of int because quantity can be a null value in the DB
    private Integer quantity;
    private String justification;
    private DispositionStatus status;
    private Instant createdAt;
    private Instant updatedAt;

    public DispositionRequest(Long id, String partNumber, DispositionType type,
                              Integer quantity, String justification,
                              DispositionStatus status, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.partNumber = partNumber;
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

    public String getPartNumber() {
        return partNumber;
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
