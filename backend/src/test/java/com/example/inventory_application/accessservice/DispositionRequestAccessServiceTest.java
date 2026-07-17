package com.example.inventory_application.accessservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import com.example.inventory_application.dto.DispositionRequestDTO;
import com.example.inventory_application.model.DispositionStatus;
import com.example.inventory_application.model.DispositionType;
import org.springframework.jdbc.core.RowMapper;
import java.time.Instant;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DispositionRequestAccessServiceTest {

    private JdbcTemplate jdbcTemplate;
    private DispositionRequestAccessService dispositionRequestAccessService;

    @BeforeEach
    void setup() {
        jdbcTemplate = mock(JdbcTemplate.class);
        dispositionRequestAccessService =
                new DispositionRequestAccessService(jdbcTemplate);
    }

    @Test
    void shouldReturnDispositionRequestsForPartNumber() {
        String partNumber = "12456-AC";
        Instant createdAt = Instant.parse("2026-01-01T10:00:00Z");
        Instant updatedAt = Instant.parse("2026-01-02T10:00:00Z");

        when(jdbcTemplate.query(
                anyString(),
                any(RowMapper.class),
                eq(partNumber)
        )).thenReturn(List.of(
                new DispositionRequestDTO(
                        12345L,
                        DispositionType.LAST_TIME_BUY,
                        50,
                        "Because I said so",
                        DispositionStatus.SUBMITTED,
                        createdAt,
                        updatedAt
                )
        ));

        List<DispositionRequestDTO> results = dispositionRequestAccessService.listPartRequests(partNumber);

        assertEquals(1, results.size());

        assertEquals(DispositionType.LAST_TIME_BUY, results.getFirst().getType());
        assertEquals(DispositionStatus.SUBMITTED, results.getFirst().getStatus());
        assertEquals(50, results.getFirst().getQuantity());
        assertEquals("Because I said so", results.getFirst().getJustification());

        verify(jdbcTemplate)
                .query(
                        contains("FROM disposition_requests"),
                        any(RowMapper.class),
                        eq(partNumber)
                );
    }
}
