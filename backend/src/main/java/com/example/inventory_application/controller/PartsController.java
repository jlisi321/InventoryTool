package com.example.inventory_application.controller;

import com.example.inventory_application.accessservice.PartsAccessService;
import com.example.inventory_application.dto.PartResponseDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/parts")
public class PartsController {
    private final PartsAccessService partDataAccessService;

    public PartsController(PartsAccessService partDataAccessService) {
        this.partDataAccessService = partDataAccessService;
    }

    @GetMapping
    public List<PartResponseDTO> listAllParts() {
        return partDataAccessService.listParts();
    }
}
