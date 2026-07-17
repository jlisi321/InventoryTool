package com.example.inventory_application.controller;

import com.example.inventory_application.accessservice.DispositionRequestAccessService;
import com.example.inventory_application.businessservice.DispositionRequestBusinessService;
import com.example.inventory_application.dto.CreateRequestDTO;
import com.example.inventory_application.dto.DispositionRequestDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dispositionRequests")
public class DispositionsRequestsController {
    private final DispositionRequestAccessService dispositionRequestAccessService;
    private final DispositionRequestBusinessService dispositionRequestBusinessService;

    public DispositionsRequestsController(DispositionRequestAccessService dispositionRequestAccessService,
                                          DispositionRequestBusinessService dispositionRequestBusinessService) {
        this.dispositionRequestAccessService = dispositionRequestAccessService;
        this.dispositionRequestBusinessService = dispositionRequestBusinessService;
    }

    @GetMapping
    public List<DispositionRequestDTO> listPartRequests( @RequestParam String partNumber ) {
        return dispositionRequestAccessService.listPartRequests(partNumber);
    }

    @PostMapping
    public ResponseEntity<DispositionRequestDTO> createDispositionRequest(
            @RequestParam String partNumber,
            @Valid @RequestBody CreateRequestDTO dto) {

        DispositionRequestDTO created = dispositionRequestBusinessService.createDispositionRequest(partNumber, dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
