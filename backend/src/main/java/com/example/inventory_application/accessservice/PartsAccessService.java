package com.example.inventory_application.accessservice;

import com.example.inventory_application.dto.PartResponseDTO;
import com.example.inventory_application.model.DispositionStatus;
import com.example.inventory_application.model.PartStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PartsAccessService {

    private final JdbcTemplate jdbcTemplate;

    public PartsAccessService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<PartResponseDTO> listParts() {
        String sql = """
            SELECT parts.part_number, parts.description, parts.monthly_demand, parts.unit_cost, parts.status,
                   disposition_requests.status AS disposition_status
            FROM parts
            LEFT JOIN disposition_requests
                ON disposition_requests.part_number = parts.part_number
                AND disposition_requests.status IN ('DRAFT', 'SUBMITTED')
            ORDER BY parts.part_number
            """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            String dispositionStatus = rs.getString("disposition_status");
            DispositionStatus activeDispositionStatus = dispositionStatus != null
                    ? DispositionStatus.valueOf(dispositionStatus)
                    : null;

            return new PartResponseDTO(
                    rs.getString("part_number"),
                    rs.getString("description"),
                    rs.getInt("monthly_demand"),
                    rs.getBigDecimal("unit_cost"),
                    PartStatus.valueOf(rs.getString("status")),
                    activeDispositionStatus
            );
        });
    }

    public boolean partExists(String partNumber) {
        String sql = "SELECT COUNT(*) FROM parts WHERE part_number = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, partNumber);
        return count != null && count > 0;
    }
}
