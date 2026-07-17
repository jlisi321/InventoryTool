package com.example.inventory_application.dto;

import com.example.inventory_application.model.PartStatus;
import com.example.inventory_application.model.DispositionStatus;

import java.math.BigDecimal;

public class PartResponseDTO {

    private String partNumber;
    private String description;
    private int monthlyDemand;
    private BigDecimal unitCost;
    private PartStatus status;
    private DispositionStatus activeDispositionStatus; // null if no active disposition

    public PartResponseDTO(String partNumber, String description, int monthlyDemand,
                           BigDecimal unitCost, PartStatus status,
                           DispositionStatus activeDispositionStatus) {
        this.partNumber = partNumber;
        this.description = description;
        this.monthlyDemand = monthlyDemand;
        this.unitCost = unitCost;
        this.status = status;
        this.activeDispositionStatus = activeDispositionStatus;
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

    public DispositionStatus getActiveDispositionStatus() {
        return activeDispositionStatus;
    }
}
