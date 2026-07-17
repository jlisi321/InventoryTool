CREATE TABLE parts (
    part_number      VARCHAR(50) PRIMARY KEY,
    description     VARCHAR(200) NOT NULL,
    monthly_demand   INTEGER NOT NULL CHECK (monthly_demand >= 0),
    unit_cost        NUMERIC(12, 2) NOT NULL CHECK (unit_cost >= 0),
    status          VARCHAR(20) NOT NULL CHECK (status IN ('ACTIVE', 'OBSOLETE'))
);