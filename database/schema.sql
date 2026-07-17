CREATE TABLE parts (
    part_number     VARCHAR(50) PRIMARY KEY,
    description     VARCHAR(200) NOT NULL,
    monthly_demand  INTEGER NOT NULL CHECK (monthly_demand >= 0),
    unit_cost       NUMERIC(12, 2) NOT NULL CHECK (unit_cost >= 0),
    status          VARCHAR(20) NOT NULL CHECK (status IN ('ACTIVE', 'OBSOLETE'))
);

CREATE TABLE disposition_requests (
    id             SERIAL PRIMARY KEY,
    part_number    VARCHAR(50) NOT NULL REFERENCES parts(part_number),
    type           VARCHAR(20) NOT NULL CHECK (type IN ('STOCK', 'LAST_TIME_BUY', 'DISCONTINUE')),
    quantity       INTEGER,
    justification  VARCHAR(250),
    status         VARCHAR(20) NOT NULL DEFAULT 'DRAFT' CHECK (status IN ('DRAFT', 'SUBMITTED', 'APPROVED', 'REJECTED')),
    created_at     TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at     TIMESTAMPTZ NOT NULL DEFAULT now(),

    -- LAST_TIME_BUY must have a positive quantity column
    CONSTRAINT last_time_buy_constraint CHECK (
        type <> 'LAST_TIME_BUY' OR (quantity IS NOT NULL AND quantity > 0)
    )
);

/*
   unique partial index created in order to ensure that the database only allows one active disposition request per part (aka in DRAFT or SUBMITTED)...
   covers race condition stretch goal, also will speed up queries
*/
CREATE UNIQUE INDEX one_active_disposition_per_part ON disposition_requests (part_number) WHERE status IN ('DRAFT', 'SUBMITTED');

/*
   another full index on part_number since ID is the primary key in this table, created in order to speed up queries.
   I could imagine inside a production app you could have millions if not tens of millions of parts,
   sequential look ups would be extremely slow
*/
CREATE INDEX disposition_part_number_full_index ON disposition_requests (part_number);