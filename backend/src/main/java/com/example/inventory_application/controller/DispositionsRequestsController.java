package com.example.inventory_application.controller;

import com.example.inventory_application.accessservice.DispositionRequestAccessService;
import com.example.inventory_application.dto.DispositionRequestDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/dispositionRequests")
public class DispositionsRequestsController {
    private final DispositionRequestAccessService dispositionRequestAccessService;

    public DispositionsRequestsController(DispositionRequestAccessService dispositionRequestAccessService) {
        this.dispositionRequestAccessService = dispositionRequestAccessService;
    }

    @GetMapping
    public List<DispositionRequestDTO> listPartRequests( @RequestParam String partNumber ) {
        return dispositionRequestAccessService.listPartRequests(partNumber);
    }
}
