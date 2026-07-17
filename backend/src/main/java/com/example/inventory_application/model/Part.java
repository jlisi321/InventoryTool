package com.example.inventory_application.model;

import java.math.BigDecimal;

public class Part {
    private String partNumber;
    private String description;
    private int monthlyDemand;
    private java.math.BigDecimal unitCost;
    private PartStatus status;

    public Part(String partNumber, String description, int monthlyDemand, java.math.BigDecimal unitCost, PartStatus status) {
        this.partNumber = partNumber;
        this.description = description;
        this.monthlyDemand = monthlyDemand;
        this.unitCost = unitCost;
        this.status = status;
    }

    public String getPartNumber() {
        return partNumber;
    }

    public String getDescription() {
        return description;
    }

    public int getMonthlyDemand() {
        return monthlyDemand;
    }

    public BigDecimal getUnitCost() {
        return unitCost;
    }

    public PartStatus getStatus() {
        return status;
    }
}
