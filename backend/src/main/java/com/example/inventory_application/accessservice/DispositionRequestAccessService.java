package com.example.inventory_application.accessservice;

import com.example.inventory_application.dto.CreateRequestDTO;
import com.example.inventory_application.dto.DispositionRequestDTO;
import com.example.inventory_application.model.DispositionStatus;
import com.example.inventory_application.model.DispositionType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DispositionRequestAccessService {

    private final JdbcTemplate jdbcTemplate;

    public DispositionRequestAccessService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<DispositionRequestDTO> listPartRequests(String partNumber) {
        String sql = """
            SELECT *
            FROM disposition_requests
            WHERE part_number = ?
            """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> new DispositionRequestDTO(
                        rs.getLong("id"),
                        DispositionType.valueOf(rs.getString("type")),
                        rs.getInt("quantity"),
                        rs.getString("justification"),
                        DispositionStatus.valueOf(rs.getString("status")),
                        rs.getTimestamp("created_at").toInstant(),
                        rs.getTimestamp("updated_at").toInstant()
                ),
                partNumber
        );
    }

    public DispositionRequestDTO createDispositionRequest(String partNumber, CreateRequestDTO dto) {
        String sql = """
            INSERT INTO disposition_requests (part_number, type, quantity, justification)
            VALUES (?, ?, ?, ?)
            RETURNING id, type, quantity, justification, status, created_at, updated_at
            """;

        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new DispositionRequestDTO(
                        rs.getLong("id"),
                        DispositionType.valueOf(rs.getString("type")),
                        rs.getInt("quantity"),
                        rs.getString("justification"),
                        DispositionStatus.valueOf(rs.getString("status")),
                        rs.getTimestamp("created_at").toInstant(),
                        rs.getTimestamp("updated_at").toInstant()
                ),
                partNumber,
                dto.getType().toString(),
                dto.getQuantity(),
                dto.getJustification()
        );
    }

    public DispositionRequestDTO findById(Long id) {
        String sql = """
            SELECT id, type, quantity, justification, status, created_at, updated_at
            FROM disposition_requests
            WHERE id = ?
            """;

        List<DispositionRequestDTO> results = jdbcTemplate.query(sql, (rs, rowNum) -> new DispositionRequestDTO(
                        rs.getLong("id"),
                        DispositionType.valueOf(rs.getString("type")),
                        rs.getInt("quantity"),
                        rs.getString("justification"),
                        DispositionStatus.valueOf(rs.getString("status")),
                        rs.getTimestamp("created_at").toInstant(),
                        rs.getTimestamp("updated_at").toInstant()
                ),
                id
        );

        return results.isEmpty() ? null : results.getFirst();
    }

    public DispositionRequestDTO updateStatus(Long id, DispositionStatus newStatus) {
        String sql = """
            UPDATE disposition_requests
            SET status = ?, updated_at = now()
            WHERE id = ?
            RETURNING id, type, quantity, justification, status, created_at, updated_at
            """;

        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new DispositionRequestDTO(
                        rs.getLong("id"),
                        DispositionType.valueOf(rs.getString("type")),
                        rs.getInt("quantity"),
                        rs.getString("justification"),
                        DispositionStatus.valueOf(rs.getString("status")),
                        rs.getTimestamp("created_at").toInstant(),
                        rs.getTimestamp("updated_at").toInstant()
                ),
                newStatus.toString(),
                id
        );
    }
}
