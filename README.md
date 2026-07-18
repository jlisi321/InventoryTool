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

```bash
cd frontend
npm install
npm run dev
```

App will run on http://localhost:5173 but make sure you have the backend already running on http://localhost:8080

### Tech Stack

- React / TypeScript / Vite
- Basic plain CSS to format things somewhat proper
- Native fetch to handle API calls

### FRONTEND RELATED NOTES 
 - I structured the app with a types folder (interfaces mirroring the DTO's from the backend), api (API functions to call backend), and components (both components that will be used on the App.tsx)
 - I skipped front end unit testing entirely due to time constraints but I would be unit testing the components individually and the API calls
 - Obviously no auth is enforced but we would at least auth with a bearer token / login creds in dev environments and potentially go through something like apigee with OAuth or whatever production auth method desired

### Where the business rules are enforced, and why
Rule 1 - State Machine transitions
- Enforced in the business logic since it is business logic, it's partially enforced in the front end as well based on what buttons are available at what times in the state machine

Rule 2 - One active request per part
- It is enforced inside the DB with a partial unique index in order to stop the race condition, enforced in the backend as well since it is business logic and we want clean errors to throw to the front, and enforced in the front end with the UI since the create form will be disabled until there is no active request

Rule 3 - LAST_TIME_BUY requiring positive quantity 
- Enforced in the database with a check constraint since it's a very basic data rule that doesn't seem it would change going forward, also it's not impossible a dev inserts directly into the DB for some reason even in a dev env. Again it is business logic so it's enforced in the backend, in the case our API calls are directly called. The front end also requires it for instant clean feedback.

Rule 4 - Justification required to submit
- Not enforced in the DB since this is a validation based on the state machine being transitioned, not really a data constraint. Enforced in the backend business logic in the case someone is making direct API calls, and enforced in the front end with the input being required. 

### What I would have done next
 - I would have wrote front end unit tests for the components and API calls
 - I would have finished more controller and access testing on the backend
 - I would have done one of the stretch goals, most likely the LAST_TIME_BUY cost estimate since it doesn't seem much to add
 - Auth would have been a big one to add given this was going towards a production app but that was out of scope
 - Adding some nice UX libraries would have been nice to make it look nice but also out of scope
