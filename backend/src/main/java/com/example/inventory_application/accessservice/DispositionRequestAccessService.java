package com.example.inventory_application.accessservice;

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
}
