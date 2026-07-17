package com.example.inventory_application.controller;

import com.example.inventory_application.accessservice.PartsAccessService;
import com.example.inventory_application.dto.PartResponseDTO;
import com.example.inventory_application.model.DispositionStatus;
import com.example.inventory_application.model.PartStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PartsControllerTest {

    private PartsAccessService partsAccessService;
    private PartsController partsController;

    @BeforeEach
    void setup() {
        partsAccessService = mock(PartsAccessService.class);
        partsController = new PartsController(partsAccessService);
    }

    @Test
    void shouldReturnAllParts() {
        List<PartResponseDTO> expectedParts = List.of(
                new PartResponseDTO(
                        "12345-AC",
                        "Front End Loader",
                        4,
                        new BigDecimal("4.25"),
                        PartStatus.ACTIVE,
                        DispositionStatus.DRAFT
                ),
                new PartResponseDTO(
                        "12456-AC",
                        "Brush Hog",
                        5,
                        new BigDecimal("5.55"),
                        PartStatus.OBSOLETE,
                        DispositionStatus.SUBMITTED
                )
        );

        when(partsAccessService.listParts())
                .thenReturn(expectedParts);

        List<PartResponseDTO> results = partsController.listAllParts();

        assertEquals(2, results.size());

        assertEquals("12345-AC", results.getFirst().getPartNumber());
        assertEquals("Front End Loader", results.getFirst().getDescription());
        assertEquals(4, results.getFirst().getMonthlyDemand());
        assertEquals(new BigDecimal("4.25"), results.getFirst().getUnitCost());
        assertEquals(PartStatus.ACTIVE, results.getFirst().getStatus());
        assertEquals(DispositionStatus.DRAFT, results.getFirst().getActiveDispositionStatus());

        assertEquals("12456-AC", results.get(1).getPartNumber());
        assertEquals("Brush Hog", results.get(1).getDescription());
        assertEquals(5, results.get(1).getMonthlyDemand());
        assertEquals(new BigDecimal("5.55"), results.get(1).getUnitCost());
        assertEquals(PartStatus.OBSOLETE, results.get(1).getStatus());
        assertEquals(DispositionStatus.SUBMITTED, results.get(1).getActiveDispositionStatus());

        verify(partsAccessService)
                .listParts();
    }

    @Test
    void shouldReturnEmptyListWhenNoPartsExist() {
        when(partsAccessService.listParts())
                .thenReturn(List.of());

        List<PartResponseDTO> results = partsController.listAllParts();

        assertTrue(results.isEmpty());

        verify(partsAccessService)
                .listParts();
    }
}