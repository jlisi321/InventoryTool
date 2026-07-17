package com.example.inventory_application.accessservice;

import com.example.inventory_application.dto.PartResponseDTO;
import com.example.inventory_application.model.DispositionStatus;
import com.example.inventory_application.model.PartStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PartsAccessServiceTest {

    private JdbcTemplate jdbcTemplate;
    private PartsAccessService partsAccessService;

    @BeforeEach
    void setup() {
        jdbcTemplate = mock(JdbcTemplate.class);
        partsAccessService = new PartsAccessService(jdbcTemplate);
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

        when(jdbcTemplate.query(
                anyString(),
                any(RowMapper.class)
        )).thenReturn(expectedParts);

        List<PartResponseDTO> results = partsAccessService.listParts();

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

        verify(jdbcTemplate)
                .query(
                        contains("FROM parts"),
                        any(RowMapper.class)
                );
    }

    @Test
    void shouldReturnEmptyListWhenNoPartsExist() {
        when(jdbcTemplate.query(
                anyString(),
                any(RowMapper.class)
        )).thenReturn(List.of());

        List<PartResponseDTO> results = partsAccessService.listParts();

        assertTrue(results.isEmpty());

        verify(jdbcTemplate)
                .query(
                        contains("FROM parts"),
                        any(RowMapper.class)
                );
    }
}