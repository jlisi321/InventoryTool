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
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DispositionRequestBusinessService {
    private final DispositionRequestAccessService dispositionRequestAccessService;
    private final PartsAccessService partsAccessService;

    public DispositionRequestBusinessService(DispositionRequestAccessService dispositionRequestAccessService,
                                      PartsAccessService partsAccessService) {
        this.dispositionRequestAccessService = dispositionRequestAccessService;
        this.partsAccessService = partsAccessService;
    }

    public DispositionRequestDTO createDispositionRequest(String partNumber, CreateRequestDTO dto) {

        if (!partsAccessService.partExists(partNumber)) {
            throw new PartNotFoundException(partNumber);
        }

        if (dto.getType() == DispositionType.LAST_TIME_BUY && (dto.getQuantity() == null || dto.getQuantity() <= 0)) {
            throw new InvalidRequestException("Type LAST_TIME_BUY requires a positive quantity");
        }

        if (hasActiveDispositionRequest(partNumber)) {
            throw new ActiveRequestExistsException(partNumber);
        }

        return dispositionRequestAccessService.createDispositionRequest(partNumber, dto);
    }

    private boolean hasActiveDispositionRequest(String partNumber) {
        List<DispositionRequestDTO> existingRequests = dispositionRequestAccessService.listPartRequests(partNumber);

        for (DispositionRequestDTO request : existingRequests) {
            if (request.getStatus() == DispositionStatus.DRAFT || request.getStatus() == DispositionStatus.SUBMITTED) {
                return true;
            }
        }

        return false;
    }

    public DispositionRequestDTO submitRequest(Long id) {
        DispositionRequestDTO existing = getRequestOrThrowError(id);

        if (existing.getStatus() != DispositionStatus.DRAFT) {
            throw new IllegalStateTransitionException(
                    "Cannot submit a disposition request with status " + existing.getStatus() + "; only DRAFT requests can be submitted");
        }

        if (existing.getJustification() == null || existing.getJustification().isBlank()) {
            throw new InvalidRequestException("A justification is required to submit a disposition request");
        }

        return dispositionRequestAccessService.updateStatus(id, DispositionStatus.SUBMITTED);
    }

    public DispositionRequestDTO approveRequest(Long id) {
        DispositionRequestDTO existing = getRequestOrThrowError(id);

        if (existing.getStatus() != DispositionStatus.SUBMITTED) {
            throw new IllegalStateTransitionException(
                    "Cannot approve a request with status " + existing.getStatus() + "; only SUBMITTED requests can be approved");
        }

        return dispositionRequestAccessService.updateStatus(id, DispositionStatus.APPROVED);
    }

    public DispositionRequestDTO rejectRequest(Long id) {
        DispositionRequestDTO existing = getRequestOrThrowError(id);

        if (existing.getStatus() != DispositionStatus.SUBMITTED) {
            throw new IllegalStateTransitionException(
                    "Cannot reject a request with status " + existing.getStatus() + "; only SUBMITTED requests can be rejected");
        }

        return dispositionRequestAccessService.updateStatus(id, DispositionStatus.REJECTED);
    }

    private DispositionRequestDTO getRequestOrThrowError(Long id) {
        DispositionRequestDTO existing = dispositionRequestAccessService.findById(id);
        if (existing == null) {
            throw new InvalidRequestException("Disposition request not found: " + id);
        }
        return existing;
    }
}
