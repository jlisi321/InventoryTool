package com.example.inventory_application.dto;

import com.example.inventory_application.model.DispositionType;

public class CreateRequestDTO {

    private DispositionType type;
    private Integer quantity;
    private String justification;

    public DispositionType getType() {
        return type;
    }

    public void setType(DispositionType type) {
        this.type = type;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getJustification() {
        return justification;
    }

    public void setJustification(String justification) {
        this.justification = justification;
    }
}
