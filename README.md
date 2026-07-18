# InventoryTool
Inventory Tool - Rootlogik 

## Steps to run locally

### Prerequisites

    - PostgreSQL 18 installed locally
    - Java 21 / Maven
    - Node 20+

### 1. Database setup and seed example data
  - connect to Postgres database with the admin role you setup during install
```bash
psql -U postgres -h localhost -p 5432
```

```sql
CREATE DATABASE inventory;
CREATE USER admin WITH PASSWORD 'admin123';
GRANT ALL PRIVILEGES ON DATABASE inventory TO admin;
\c inventory
GRANT ALL ON SCHEMA public TO admin;
\q
```

Create schema, load sample parts data, verify it was loaded:

```bash
psql -U admin -d inventory -h localhost -p 5432 -f database/schema.sql
psql -U admin -d inventory -h localhost -p 5432 -f database/sample_data.sql
psql -U admin -d inventory -h localhost -p 5432 -c "SELECT * FROM parts;"
psql -U admin -d inventory -h localhost -p 5432 -c "SELECT * FROM disposition_requests;"
```

### DATABASE RELATED NOTES 
 - I made all columns in parts necessary besides description as it just makes sense to me. If a user doesn't have monthlyDemand or unitCost yet they can enter 0. A description can be empty, but I prefer it goes into the DB as 0 characters... not null.
 - I create dispositionRequests table to have an auto incrementing ID, tied to a partNumber from parts. Status will default at draft if not provided for some reason even though it will always be provided from the app, but it just made sense to default it to DRAFT since the state machine says that the first state must always be draft. Timestamps for creation and updates were also added since it's useful to show on the UI potentially.
 - Inside dispositionRequests I decided to enforce the rule that quantity must be > 0 if type is LAST_TIME_BUY. I considered that putting this rule in the DB and in the backend requires more maintenance, but I felt like this validation was a very basic check that will most likely hold forever. If the business logic were to change to something like, quantity > 10 if type is LAST TIME BUY, I would most likely only change this logic in the backend going forward but for a basic > 0 check I added it to the DB to ensure this rule can't be broken regardless of backend changes.
 - I created a unique partial index in disposition request on part number where status is draft or submitted in order to enforce one active disposition at a time. I will also add backend business logic to cover this in order to provide a clear error but this unique index will cover the race conditions that backend can't cover easily
 - lastly I added a full index on part number inside disposition requests table to speed up the queries since ID is the primary key, i could imagine inside a production app there could be millions of disposition requests... sequential look ups could be a very long look up. No full index needs to be added on the parts table since part_number is already the primary key

### 2. Backend startup

```bash
cd backend
mvn spring-boot:run
```

The app will start on http://localhost:8080 

### API's
`GET /parts`
Lists all parts, including active disposition status
```
GET http://localhost:8080/parts
```
```json
[
  {
    "partNumber": "10245-AC",
    "description": "Front end loader",
    "monthlyDemand": 10,
    "unitCost": 4.25,
    "status": "ACTIVE",
    "activeDispositionStatus": null
  }
]
```
`GET /dispositionRequests?partNumber={partNumber}`
Lists all disposition requests for a given part, including the full history.
```
GET http://localhost:8080/dispositionRequests?partNumber=12456-AC
```
```json
[
  {
    "id": 5,
    "type": "LAST_TIME_BUY",
    "quantity": 50,
    "justification": "test",
    "status": "SUBMITTED",
    "createdAt": "2026-07-17T18:03:47.883566Z",
    "updatedAt": "2026-07-17T18:03:47.883566Z"
  }
]
```
`POST /dispositionRequests?partNumber={partNumber}`
Creates a new disposition request for a given part. Starts with status as `DRAFT`.
```
POST http://localhost:8080/dispositionRequests?partNumber=10245-AC
Content-Type: application/json

{
  "type": "STOCK",
  "quantity": null,
  "justification": "justified"
}
```
`PATCH /dispositionRequests/{id}/submit`
Transitions state of request from `DRAFT` to `SUBMITTED`. Requires a justification.

`PATCH /dispositionRequests/{id}/approve`
Transitions state of request from `SUBMITTED` to `APPROVED`.

`PATCH /dispositionRequests/{id}/reject`
Transitions state of request from `SUBMITTED` to `REJECTED`.

### BACKEND RELATED NOTES
 - I seperated the structure with controller (HTTP routes), businessservice (backend logic), accessservice (data access), model(java classes mirroring database rows), dto (request and response classes), exceptions
 - All state machine logic is inside the business service disposition class since this is business logic
 - The logic for having one active request per part is enforced in the DB but I also reenforced it inside the business logic so that the front end can be given clear errors
 - The LAST_TIME_BUY logic is enforced in the DB but I also enforced it inside the business logic again for clean exceptions
 - added CORS rules to allow the react frontend to make all necessary API calls. In prod app this would obviously be much more strict rules.

### 3. Frontend startup

### FRONTEND RELATED NOTES 
 - I skipped API call unit testing due to time constraints but I would be unit testing every API call, but I honestly already tested these on the backend for this project which isn't sufficient for production apps but I wanted to focus on finishing
 - Obviously no auth is enforced but we would at least auth with a bearer token / login creds in dev environments and potentially go through something like apigee with OAuth or whatever production auth method desired
