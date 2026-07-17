package com.example.inventory_application.businessservice;

import com.example.inventory_application.accessservice.DispositionRequestAccessService;
import com.example.inventory_application.accessservice.PartsAccessService;
import com.example.inventory_application.dto.CreateRequestDTO;
import com.example.inventory_application.dto.DispositionRequestDTO;
import com.example.inventory_application.exception.ActiveRequestExistsException;
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
}
