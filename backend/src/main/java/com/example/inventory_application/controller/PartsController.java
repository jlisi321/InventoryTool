package com.example.inventory_application.controller;

import com.example.inventory_application.accessservice.PartsAccessService;
import com.example.inventory_application.dto.PartResponseDTO;
import org.springframework.web.bind.annotation.*;

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
