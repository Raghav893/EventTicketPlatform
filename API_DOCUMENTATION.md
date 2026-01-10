# Event Ticket Platform API Documentation

Base URL: `http://localhost:8080` (Default Spring Boot port)

## Authentication

All endpoints except `/auth/**` require a valid JWT token in the `Authorization` header.
Format: `Bearer <your_token>`

### 1. Register User
Registers a new user in the system.

- **URL:** `/auth/register`
- **Method:** `POST`
- **Auth:** Public
- **Request Body:**
  ```json
  {
    "username": "johndoe",
    "password": "securepassword",
    "Role": "ORGANIZER" 
  }
  ```
  *Note: Roles can be "ORGANIZER", "ATTENDEE", or "STAFF".*

- **Response (201 Created):**
  ```json
  {
    "userId": 1,
    "username": "johndoe",
    "role": "ORGANIZER",
    ...
  }
  ```

### 2. Login
Authenticates a user and returns a JWT token.

- **URL:** `/auth/login`
- **Method:** `POST`
- **Auth:** Public
- **Request Body:**
  ```json
  {
    "username": "johndoe",
    "password": "securepassword"
  }
  ```

- **Response (200 OK):**
  ```text
  eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2huZG9l... (JWT Token)
  ```

---

## Events

### 1. Create Event
Creates a new event. Only accessible by users with the `ORGANIZER` role.

- **URL:** `/events/create`
- **Method:** `POST`
- **Auth:** Organizer
- **Request Body:**
  ```json
  {
    "title": "Tech Conference 2024",
    "description": "Annual tech gathering",
    "location": "Convention Center",
    "dateTime": "2024-12-01T09:00:00",
    "status": "PUBLISHED"
  }
  ```
  *Note: `status` can be "PUBLISHED", "DRAFT", etc.*

- **Response (201 Created):** Returns the created event object.

### 2. Get All Events
Retrieves a list of all published events.

- **URL:** `/events`
- **Method:** `GET`
- **Auth:** Authenticated User

### 3. Get Event by ID
Retrieves a specific event by its ID.

- **URL:** `/events/{id}`
- **Method:** `GET`
- **Auth:** Authenticated User

### 4. Get My Events (Organizer)
Retrieves all events created by the currently logged-in organizer.

- **URL:** `/events/my-events`
- **Method:** `GET`
- **Auth:** Organizer

### 5. Get My Events by Status
Retrieves organizer's events filtered by status.

- **URL:** `/events/my-events/{status}`
- **Method:** `GET`
- **Auth:** Organizer
- **Example:** `/events/my-events/PUBLISHED`

### 6. Update Event
Updates an existing event. Only the creator of the event can update it.

- **URL:** `/events/{id}`
- **Method:** `PUT`
- **Auth:** Organizer (Owner)
- **Request Body:** (Same as Create Event)

### 7. Delete Event
Deletes an event. Only the creator can delete it.

- **URL:** `/events/{id}`
- **Method:** `DELETE`
- **Auth:** Organizer (Owner)

---

## Ticket Types

### 1. Create Ticket Type
Adds a ticket type (e.g., VIP, General Admission) to an event.

- **URL:** `/events/{eventId}/ticket-types`
- **Method:** `POST`
- **Auth:** Organizer (Event Owner)
- **Request Body:**
  ```json
  {
    "name": "VIP Admission",
    "ticketQuantity": 100,
    "remaining": 100,
    "CreatedAt": "2024-01-10T10:00:00"
  }
  ```

- **Response (201 Created):** Returns the created ticket type.

### 2. Update Ticket Type
Updates a ticket type.

- **URL:** `/events/{eventId}/ticket-types/{ticketTypeId}`
- **Method:** `PUT`
- **Auth:** Organizer (Event Owner)
- **Request Body:** (Same as Create Ticket Type)

### 3. Delete Ticket Type
Removes a ticket type from an event.

- **URL:** `/events/{eventId}/ticket-types/{ticketTypeId}`
- **Method:** `DELETE`
- **Auth:** Organizer (Event Owner)

---

## Ticket Purchase

### 1. Purchase Ticket
Allows an attendee to purchase a ticket for an event.

- **URL:** `/events/{eventId}/ticket-types/{ticketTypeId}/purchase`
- **Method:** `POST`
- **Auth:** Attendee
- **Response (201 Created):** Returns the purchased ticket details including a `ticketCode` (UUID).

### 2. Get My Tickets
Retrieves all tickets purchased by the logged-in attendee.

- **URL:** `/my-tickets`
- **Method:** `GET`
- **Auth:** Attendee

---

## Ticket Validation

### 1. Validate Ticket
Validates a ticket (e.g., via QR scan) at the event. Only accessible by Staff.

- **URL:** `/tickets/{ticketCode}/validate`
- **Method:** `POST`
- **Auth:** Staff
- **Response (200 OK):** Returns the validation record.

### 2. Get Ticket Info
Retrieves information about a specific ticket using its unique code.

- **URL:** `/ticket/{ticketCode}`
- **Method:** `GET`
- **Auth:** Staff, Organizer (Event Owner), or Attendee (Ticket Owner)
- **Response:**
  ```json
  {
    "ticketStatus": "UNUSED",
    "attendeeUsername": "johndoe",
    "event_name": "Tech Conference 2024"
  }
  ```

### 3. Get Validated Tickets for Event
Retrieves a list of all tickets that have been validated for a specific event.

- **URL:** `/events/{eventId}/validated-tickets`
- **Method:** `GET`
- **Auth:** Organizer (Event Owner)
