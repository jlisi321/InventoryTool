package com.example.inventory_application.controller;

import com.example.inventory_application.accessservice.DispositionRequestAccessService;
import com.example.inventory_application.businessservice.DispositionRequestBusinessService;
import com.example.inventory_application.dto.DispositionRequestDTO;
import com.example.inventory_application.model.DispositionStatus;
import com.example.inventory_application.model.DispositionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DispositionsRequestsControllerTest {

    private DispositionRequestAccessService dispositionRequestAccessService;
    private DispositionsRequestsController dispositionsRequestsController;
    private DispositionRequestBusinessService dispositionRequestBusinessService;

    @BeforeEach
    void setup() {
        dispositionRequestAccessService = mock(DispositionRequestAccessService.class);
        dispositionRequestBusinessService = mock(DispositionRequestBusinessService.class);

        dispositionsRequestsController =
                new DispositionsRequestsController(dispositionRequestAccessService, dispositionRequestBusinessService);
    }

    @Test
    void shouldReturnDispositionRequestsForPartNumber() {
        String partNumber = "12456-AC";

        List<DispositionRequestDTO> expectedRequests = List.of(
                new DispositionRequestDTO(
                        1L,
                        DispositionType.LAST_TIME_BUY,
                        50,
                        "Customer demand still exists",
                        DispositionStatus.SUBMITTED,
                        Instant.parse("2026-01-01T10:00:00Z"),
                        Instant.parse("2026-01-02T10:00:00Z")
                )
        );

        when(dispositionRequestAccessService.listPartRequests(partNumber))
                .thenReturn(expectedRequests);

        List<DispositionRequestDTO> results =
                dispositionsRequestsController.listPartRequests(partNumber);

        assertEquals(1, results.size());

        assertEquals(1L, results.getFirst().getId());
        assertEquals(DispositionType.LAST_TIME_BUY, results.getFirst().getType());
        assertEquals(50, results.getFirst().getQuantity());
        assertEquals("Customer demand still exists",
                results.getFirst().getJustification());
        assertEquals(DispositionStatus.SUBMITTED,
                results.getFirst().getStatus());

        verify(dispositionRequestAccessService)
                .listPartRequests(partNumber);
    }

    @Test
    void shouldReturnEmptyListWhenNoDispositionRequestsExist() {
        String partNumber = "99999-XX";

        when(dispositionRequestAccessService.listPartRequests(partNumber))
                .thenReturn(List.of());

        List<DispositionRequestDTO> results =
                dispositionsRequestsController.listPartRequests(partNumber);

        assertTrue(results.isEmpty());

        verify(dispositionRequestAccessService)
                .listPartRequests(partNumber);
    }
}