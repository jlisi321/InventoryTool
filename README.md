# InventoryTool
Inventory Tool - Rootlogik 


## Steps to run locally

### Prerequisites

    - PostgreSQL 18 installed locally
    - Java 21 / Maven
    - Node 20+

### 1. Database setup and seed example data

```bash
psql -U postgres -h localhost -p 5432
```

```sql
CREATE DATABASE inventory;
CREATE USER admin WITH PASSWORD 'admin123';
GRANT ALL PRIVILEGES ON DATABASE inventory TO admin;
\q
```

Create schema:

```bash
psql -U parts_user -d parts -h localhost -p 5432 -f db/schema.sql
```

### NOTES 
 - I made all columns in parts necessary besides description as it just makes sense to me. If a user doesn't have monthlyDemand or unitCost yet they can enter 0. A description can be empty, but I prefer it goes into the DB as 0 characters... not null.