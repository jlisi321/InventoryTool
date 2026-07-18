package com.example.inventory_application.businessservice;

import com.example.inventory_application.accessservice.DispositionRequestAccessService;
import com.example.inventory_application.accessservice.PartsAccessService;
import com.example.inventory_application.dto.CreateRequestDTO;
import com.example.inventory_application.dto.DispositionRequestDTO;
import com.example.inventory_application.exception.ActiveRequestExistsException;
import com.example.inventory_application.exception.IllegalStateTransitionException;
import com.example.inventory_application.exception.InvalidRequestException;
import com.example.inventory_application.exception.PartNotFoundException;
import com.example.inventory_application.model.DispositionStatus;
import com.example.inventory_application.model.DispositionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DispositionRequestBusinessServiceTest {

    private DispositionRequestAccessService dispositionRequestAccessService;
    private PartsAccessService partsAccessService;
    private DispositionRequestBusinessService dispositionRequestBusinessService;

    @BeforeEach
    void setup() {
        dispositionRequestAccessService = mock(DispositionRequestAccessService.class);
        partsAccessService = mock(PartsAccessService.class);
        dispositionRequestBusinessService = new DispositionRequestBusinessService(dispositionRequestAccessService, partsAccessService);
    }

    @Test
    void shouldCreateDispositionRequest() {
        String partNumber = "10245-AC";
        CreateRequestDTO dto = new CreateRequestDTO();
        dto.setType(DispositionType.STOCK);
        dto.setQuantity(null);
        dto.setJustification("just 1");

        when(partsAccessService.partExists(partNumber)).thenReturn(true);
        when(dispositionRequestAccessService.listPartRequests(partNumber))
                .thenReturn(List.of());

        DispositionRequestDTO expected = new DispositionRequestDTO(
                1L, DispositionType.STOCK, null, "just 1",
                DispositionStatus.DRAFT, Instant.now(), Instant.now());
        when(dispositionRequestAccessService.createDispositionRequest(partNumber, dto))
                .thenReturn(expected);

        DispositionRequestDTO result = dispositionRequestBusinessService.createDispositionRequest(partNumber, dto);

        assertEquals(expected, result);
        verify(dispositionRequestAccessService).createDispositionRequest(partNumber, dto);
    }

    @Test
    void shouldErrorWhenPartDoesNotExist() {
        String partNumber = "99999-ZZ";
        CreateRequestDTO dto = new CreateRequestDTO();
        dto.setType(DispositionType.STOCK);
        dto.setJustification("test");

        when(partsAccessService.partExists(partNumber)).thenReturn(false);

        assertThrows(PartNotFoundException.class, () -> dispositionRequestBusinessService.createDispositionRequest(partNumber, dto));

        verify(dispositionRequestAccessService, never()).createDispositionRequest(any(), any());
    }

    @Test
    void shouldErrorWhenDraftRequestExists() {
        String partNumber = "10245-AC";
        CreateRequestDTO dto = new CreateRequestDTO();
        dto.setType(DispositionType.STOCK);
        dto.setJustification("Another one");

        when(partsAccessService.partExists(partNumber)).thenReturn(true);
        when(dispositionRequestAccessService.listPartRequests(partNumber))
                .thenReturn(List.of(new DispositionRequestDTO(
                        1L, DispositionType.STOCK, null, "Existing",
                        DispositionStatus.DRAFT, Instant.now(), Instant.now())));

        assertThrows(ActiveRequestExistsException.class, () -> dispositionRequestBusinessService.createDispositionRequest(partNumber, dto));

        verify(dispositionRequestAccessService, never()).createDispositionRequest(any(), any());
    }

    @Test
    void shouldErrorWhenSubmittedRequestExists() {
        String partNumber = "10245-AC";
        CreateRequestDTO dto = new CreateRequestDTO();
        dto.setType(DispositionType.DISCONTINUE);
        dto.setJustification("Another one");

        when(partsAccessService.partExists(partNumber)).thenReturn(true);
        when(dispositionRequestAccessService.listPartRequests(partNumber))
                .thenReturn(List.of(new DispositionRequestDTO(
                        1L, DispositionType.LAST_TIME_BUY, 50, "Existing",
                        DispositionStatus.SUBMITTED, Instant.now(), Instant.now())));

        assertThrows(ActiveRequestExistsException.class, () -> dispositionRequestBusinessService.createDispositionRequest(partNumber, dto));
    }

    @Test
    void shouldAllowNewRequestWhenOnlyTerminalRequestsExist() {
        String partNumber = "10245-AC";
        CreateRequestDTO dto = new CreateRequestDTO();
        dto.setType(DispositionType.STOCK);
        dto.setJustification("test");

        when(partsAccessService.partExists(partNumber)).thenReturn(true);
        when(dispositionRequestAccessService.listPartRequests(partNumber))
                .thenReturn(List.of(
                        new DispositionRequestDTO(1L, DispositionType.DISCONTINUE, null,
                                "Rejected one", DispositionStatus.REJECTED, Instant.now(), Instant.now()),
                        new DispositionRequestDTO(2L, DispositionType.STOCK, null,
                                "Approved one", DispositionStatus.APPROVED, Instant.now(), Instant.now())
                ));

        DispositionRequestDTO expected = new DispositionRequestDTO(
                3L, DispositionType.STOCK, null, "New attempt after rejection",
                DispositionStatus.DRAFT, Instant.now(), Instant.now());
        when(dispositionRequestAccessService.createDispositionRequest(partNumber, dto))
                .thenReturn(expected);

        DispositionRequestDTO result = dispositionRequestBusinessService.createDispositionRequest(partNumber, dto);

        assertEquals(expected, result);
    }

    @Test
    void shouldErrorWhenLastTimeBuyHasNullQuantity() {
        CreateRequestDTO dto = new CreateRequestDTO();
        dto.setType(DispositionType.LAST_TIME_BUY);
        dto.setQuantity(null);
        dto.setJustification("null qty");

        when(partsAccessService.partExists("10245-AC")).thenReturn(true);

        assertThrows(InvalidRequestException.class, () -> dispositionRequestBusinessService.createDispositionRequest("10245-AC", dto));
    }

    @Test
    void shouldErrorWhenLastTimeBuyHasZeroQuantity() {
        CreateRequestDTO dto = new CreateRequestDTO();
        dto.setType(DispositionType.LAST_TIME_BUY);
        dto.setQuantity(0);
        dto.setJustification("zero qty");

        when(partsAccessService.partExists("10245-AC")).thenReturn(true);

        assertThrows(InvalidRequestException.class, () -> dispositionRequestBusinessService.createDispositionRequest("10245-AC", dto));
    }

    @Test
    void shouldErrorWhenLastTimeBuyHasNegativeQuantity() {
        CreateRequestDTO dto = new CreateRequestDTO();
        dto.setType(DispositionType.LAST_TIME_BUY);
        dto.setQuantity(-5);
        dto.setJustification("neg qty");

        when(partsAccessService.partExists("10245-AC")).thenReturn(true);

        assertThrows(InvalidRequestException.class, () -> dispositionRequestBusinessService.createDispositionRequest("10245-AC", dto));
    }

    @Test
    void shouldSubmitDraftRequest() {
        DispositionRequestDTO draft = new DispositionRequestDTO(
                1L, DispositionType.STOCK, null, "justification",
                DispositionStatus.DRAFT, Instant.now(), Instant.now());
        DispositionRequestDTO submitted = new DispositionRequestDTO(
                1L, DispositionType.STOCK, null, "justification",
                DispositionStatus.SUBMITTED, Instant.now(), Instant.now());

        when(dispositionRequestAccessService.findById(1L)).thenReturn(draft);
        when(dispositionRequestAccessService.updateStatus(1L, DispositionStatus.SUBMITTED))
                .thenReturn(submitted);

        DispositionRequestDTO result = dispositionRequestBusinessService.submitRequest(1L);

        assertEquals(DispositionStatus.SUBMITTED, result.getStatus());
    }

    @Test
    void shouldErrorWhenSubmittingNonDraftRequest() {
        DispositionRequestDTO alreadySubmitted = new DispositionRequestDTO(
                1L, DispositionType.STOCK, null, "test",
                DispositionStatus.SUBMITTED, Instant.now(), Instant.now());

        when(dispositionRequestAccessService.findById(1L)).thenReturn(alreadySubmitted);

        assertThrows(IllegalStateTransitionException.class, () -> dispositionRequestBusinessService.submitRequest(1L));

        verify(dispositionRequestAccessService, never()).updateStatus(any(), any());
    }

    @Test
    void shouldErrorWhenSubmittingWithoutJustification() {
        DispositionRequestDTO draftNoJustification = new DispositionRequestDTO(
                1L, DispositionType.STOCK, null, null,
                DispositionStatus.DRAFT, Instant.now(), Instant.now());

        when(dispositionRequestAccessService.findById(1L)).thenReturn(draftNoJustification);

        assertThrows(InvalidRequestException.class, () -> dispositionRequestBusinessService.submitRequest(1L));
    }

    @Test
    void shouldApproveSubmittedRequest() {
        DispositionRequestDTO submitted = new DispositionRequestDTO(
                1L, DispositionType.STOCK, null, "test",
                DispositionStatus.SUBMITTED, Instant.now(), Instant.now());
        DispositionRequestDTO approved = new DispositionRequestDTO(
                1L, DispositionType.STOCK, null, "test",
                DispositionStatus.APPROVED, Instant.now(), Instant.now());

        when(dispositionRequestAccessService.findById(1L)).thenReturn(submitted);
        when(dispositionRequestAccessService.updateStatus(1L, DispositionStatus.APPROVED)).thenReturn(approved);

        DispositionRequestDTO result = dispositionRequestBusinessService.approveRequest(1L);

        assertEquals(DispositionStatus.APPROVED, result.getStatus());
    }

    @Test
    void shouldErrorWhenApprovingDraftRequest() {
        DispositionRequestDTO draft = new DispositionRequestDTO(
                1L, DispositionType.STOCK, null, "test",
                DispositionStatus.DRAFT, Instant.now(), Instant.now());

        when(dispositionRequestAccessService.findById(1L)).thenReturn(draft);

        assertThrows(IllegalStateTransitionException.class, () -> dispositionRequestBusinessService.approveRequest(1L));
    }

    @Test
    void shouldErrorWhenApprovingAlreadyApprovedRequest() {
        DispositionRequestDTO approved = new DispositionRequestDTO(
                1L, DispositionType.STOCK, null, "test",
                DispositionStatus.APPROVED, Instant.now(), Instant.now());

        when(dispositionRequestAccessService.findById(1L)).thenReturn(approved);

        assertThrows(IllegalStateTransitionException.class, () -> dispositionRequestBusinessService.approveRequest(1L));
    }

    @Test
    void shouldRejectSubmittedRequest() {
        DispositionRequestDTO submitted = new DispositionRequestDTO(
                1L, DispositionType.STOCK, null, "test",
                DispositionStatus.SUBMITTED, Instant.now(), Instant.now());
        DispositionRequestDTO rejected = new DispositionRequestDTO(
                1L, DispositionType.STOCK, null, "test",
                DispositionStatus.REJECTED, Instant.now(), Instant.now());

        when(dispositionRequestAccessService.findById(1L)).thenReturn(submitted);
        when(dispositionRequestAccessService.updateStatus(1L, DispositionStatus.REJECTED))
                .thenReturn(rejected);

        DispositionRequestDTO result = dispositionRequestBusinessService.rejectRequest(1L);

        assertEquals(DispositionStatus.REJECTED, result.getStatus());
    }

    @Test
    void shouldErrorWhenRejectingTerminalRequest() {
        DispositionRequestDTO rejected = new DispositionRequestDTO(
                1L, DispositionType.STOCK, null, "test",
                DispositionStatus.REJECTED, Instant.now(), Instant.now());

        when(dispositionRequestAccessService.findById(1L)).thenReturn(rejected);

        assertThrows(IllegalStateTransitionException.class, () -> dispositionRequestBusinessService.rejectRequest(1L));
    }

    @Test
    void shouldErrorWhenSubmittingNonExistentRequest() {
        when(dispositionRequestAccessService.findById(999L)).thenReturn(null);

        assertThrows(InvalidRequestException.class, () -> dispositionRequestBusinessService.submitRequest(999L));
    }
}