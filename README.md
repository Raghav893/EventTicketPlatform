# Event Ticketing Platform – Backend

## 1. Project Overview

### Problem Statement
Managing event logistics involves complex challenges regarding inventory management, secure access control, and fraud prevention. Traditional ticketing systems often lack granular role-based security or require expensive proprietary hardware for validation.

### Solution Overview
The Event Ticketing Platform is a robust, backend-only RESTful API designed to manage the full lifecycle of event ticketing. Built with Java 21 and Spring Boot, it provides secure mechanisms for event creation, inventory-safe ticket purchasing, and physical entry validation via QR codes (simulated via UUIDs).

### Key Features
*   **Secure Authentication:** Stateless JWT-based authentication ensures secure access without server-side session storage.
*   **Role-Based Access Control (RBAC):** Strict separation of duties between Organizers, Staff, and Attendees.
*   **Inventory Management:** Concurrency-safe logic prevents ticket overselling.
*   **QR Code Validation:** A dedicated workflow for gate staff to validate tickets in real-time, preventing duplicate entry.
*   **Rate Limiting:** Redis-backed rate limiting protects the API from abuse.

### Role-Based Capabilities
1.  **ORGANIZER:** Authorized to create events, define ticket types, set prices, and view sales data. They possess ownership rights over the events they create.
2.  **ATTENDEE:** Can browse events, view availability, and purchase tickets.
3.  **STAFF:** restricted solely to the operational aspect of validating tickets at the event venue. They cannot modify event details or purchase history.

## 2. Architecture Overview

### High-Level Architecture
The system follows a standard Layered Architecture pattern using Spring Boot:

1.  **Presentation Layer (Controllers):** Handles HTTP requests and maps JSON payloads to DTOs.
2.  **Security Layer (Filters):** Intercepts requests for Rate Limiting (Redis) and Authentication (JWT).
3.  **Service Layer:** Contains business logic, transaction management, and role/ownership verification.
4.  **Data Access Layer (Repositories):** Interfaces with the MySQL database using Spring Data JPA.

### Request Flow
1.  **Client Request:** An HTTP request is sent to an API endpoint.
2.  **Rate Limiting:** The `RateLimitFilter` checks Redis to ensure the client has not exceeded the allowed request threshold.
3.  **Authentication:** The `JwtFilter` validates the `Authorization` header. If valid, the user identity and roles are loaded into the `SecurityContext`.
4.  **Controller:** The request is routed to the appropriate controller method.
5.  **Business Logic:** The Service layer executes logic (e.g., checking inventory).
6.  **Persistence:** The Repository layer performs CRUD operations on the MySQL database.
7.  **Response:** The system returns a structured DTO and an appropriate HTTP status code.

### Role & Ownership Enforcement
Security is enforced at the method level. Beyond standard role checks (e.g., `@PreAuthorize("hasRole('ORGANIZER')")`), the service layer implements "Ownership Logic." For example, an Organizer can only modify events they personally created. This prevents horizontal privilege escalation.

## 3. Data Model

### Entity Descriptions
*   **Users:** Stores authentication credentials, distinct roles (Organizer, Attendee, Staff), and profile information.
*   **Events:** Represents an event instance. Contains metadata (name, date, location) and is linked to a specific Organizer (User).
*   **TicketType:** Represents a category of ticket for a specific event (e.g., VIP, General Admission). It defines the price and the total stock available.
*   **Ticket:** Represents a purchased instance. It contains a unique UUID (acting as the QR code), current status (VALID/USED), and links the Attendee to a specific TicketType.
*   **TicketValidation:** An audit log entity created when a ticket is successfully scanned. It records the timestamp, the Staff member who performed the scan, and references the Ticket.

### Relationships
*   **User to Events:** One-to-Many. One Organizer can manage multiple Events.
*   **Event to TicketType:** One-to-Many. An Event can have multiple tiers (VIP, Regular).
*   **TicketType to Ticket:** One-to-Many. A specific TicketType tracks multiple issued Tickets.
*   **Ticket to TicketValidation:** One-to-One. A ticket can be successfully validated for entry only once.

### Design Note: Entity Isolation
The `TicketValidation` entity specifically references the `Ticket` and the `Staff` member, but does not directly reference the `Event`. This normalization ensures that validation logic relies strictly on the validity of the specific ticket UUID. The Event association is derived transitively through the Ticket -> TicketType -> Event chain, reducing data redundancy.

## 4. Authentication & Authorization

### JWT Generation and Validation
The system utilizes JSON Web Tokens (JWT) for stateless authentication.
1.  **Login:** Users submit credentials via the `/login` endpoint.
2.  **Generation:** Upon successful authentication, the server generates a signed JWT containing the username, issued-at time, expiration time, and assigned roles.
3.  **Validation:** Subsequent requests must include the JWT in the `Authorization: Bearer <token>` header. The `JwtFilter` validates the token, verifies the cryptographic signature, and checks for expiration.

### SecurityContext Usage
Once a token is validated, a `UsernamePasswordAuthenticationToken` is created and stored in Spring Security's `SecurityContext`. This allows the application to globally access the details of the currently authenticated user for audit trails and ownership checks.

## 5. Core Business Flows

### Event Creation Flow
1.  User with `ORGANIZER` role submits event details.
2.  System validates dates (e.g., event cannot be in the past).
3.  Event is saved with the current user explicitly set as the owner.

### Ticket Purchase Flow (Inventory-Safe)
1.  `ATTENDEE` requests to buy a specific `TicketType`.
2.  **Transaction Start:**
3.  System locks or atomically checks the inventory count for that `TicketType`.
4.  If `current_sales < total_allocation`:
    *   Increment sales count.
    *   Generate a new `Ticket` entity with a random UUID.
    *   Set status to `VALID`.
5.  **Transaction Commit.**
6.  If inventory is full, throw an exception and roll back.

### Ticket Validation Flow (QR-Based)
1.  `STAFF` scans the ticket UUID (sends request to backend).
2.  System retrieves the `Ticket` by UUID.
3.  **Checks:**
    *   Does the ticket exist?
    *   Is the status `VALID`?
4.  **Action:**
    *   If valid: Change status to `USED`.
    *   Create a `TicketValidation` record linking the Staff ID and Ticket ID.
    *   Return success message.
    *   If already `USED`: Return error indicating duplicate entry attempt.

## 6. API Endpoints

### Authentication
*   **POST /auth/register:** Creates a new user account.
*   **POST /auth/login:** Authenticates credentials and returns a JWT.

### Event Management
*   **POST /events:** Create a new event (Organizer only).
*   **GET /events:** List all active events (Public).
*   **GET /events/{id}:** Get details for a specific event.

### Ticket Management
*   **POST /ticket-types:** Add a ticket category to an event (Organizer only, owner check applies).
*   **POST /tickets/purchase:** Purchase a ticket for a specific ticket type (Attendee only).
*   **GET /tickets/my-tickets:** View purchase history (Attendee only).

### Validation
*   **POST /validation/validate:** Verify and redeem a ticket using its UUID (Staff only).

## 7. Error Handling Strategy

The application employs a global exception handler to return consistent JSON error responses.

*   **200 OK:** Operation successful.
*   **201 Created:** Resource successfully created.
*   **400 Bad Request:** Validation failure (e.g., missing fields, invalid email format).
*   **401 Unauthorized:** Missing or invalid JWT token.
*   **403 Forbidden:** User is authenticated but lacks the specific role (e.g., Attendee trying to create an event) or does not own the resource (Organizer trying to edit another Organizer's event).
*   **404 Not Found:** Resource (Event, Ticket, User) does not exist.
*   **409 Conflict:** Business logic violation, such as attempting to buy a ticket when inventory is zero, or attempting to validate an already used ticket.

## 8. Concurrency & Data Integrity

### Preventing Overselling
The system utilizes database transaction isolation and atomic updates when modifying ticket inventory. By ensuring the "check-then-update" logic happens within a transactional boundary, the system prevents race conditions where two users might buy the last ticket simultaneously.

### Irreversible Validation
Ticket validation is a one-way state transition (VALID -> USED). This design choice ensures strict security at the physical venue. Once a ticket is scanned, it cannot be reset, preventing a single ticket from being passed back in line to admit another person.

## 9. Docker & Deployment

### Containerization
*   **Application Service:** Builds the Java JAR using Maven and runs it on a JRE 21 image.
*   **Database Service:** Official MySQL container.
*   **Redis Service:** Official Redis container used for caching and rate limiting.

### Docker Compose
The `docker-compose.yml` orchestrates these three services. It defines a dedicated network allowing the application to communicate with MySQL and Redis using their service names (`mysql`, `redis`) as hostnames.

### Environment Variables
Sensitive configuration such as database credentials, the JWT secret key, and server ports are injected via environment variables defined in the compose file or a `.env` file, adhering to 12-factor app principles.

## 10. Testing Strategy

### API Testing
Testing is primarily conducted via the provided Postman Collection.

### Suggested Test Order
1.  **Register:** Create users for all three roles (Organizer, Staff, Attendee).
2.  **Login:** Authenticate as Organizer to get a token.
3.  **Create Event:** Use the Organizer token to create an event.
4.  **Create Ticket Type:** Define a "VIP" ticket with a stock of 10.
5.  **Login:** Authenticate as Attendee.
6.  **Purchase:** Buy a ticket using the Attendee token; save the returned Ticket UUID.
7.  **Login:** Authenticate as Staff.
8.  **Validate:** Use the Staff token and the Ticket UUID to validate entry.

### Negative Test Cases
*   **Overselling:** Attempt to buy 11 tickets when stock is 10.
*   **Double Entry:** Attempt to validate the same UUID twice.
*   **Unauthorized Access:** Attendee attempts to create an event.
*   **Cross-Ownership:** Organizer A attempts to modify Organizer B's event.

## 11. Design Decisions & Trade-offs

### JWT vs. OAuth
JWT was chosen for its simplicity and statelessness. Since this is a self-contained platform without third-party integration (e.g., "Login with Google"), a full OAuth2 implementation would introduce unnecessary complexity.

### UUID for Ticket Codes
Sequential integers are predictable, allowing malicious users to guess valid ticket IDs. UUIDs provide a sufficiently large keyspace to make guessing a valid ticket ID statistically impossible, serving as a secure surrogate for a QR code.

### DTOs vs. Entities
The API never returns raw Database Entities. Data Transfer Objects (DTOs) are used to decouple the internal database schema from the external API contract. This prevents infinite recursion in JSON serialization (due to bidirectional relationships) and prevents leakage of sensitive data (like password hashes).

### Redis
Redis is utilized specifically for rate limiting. Its in-memory speed is essential for tracking request counts per IP/User in real-time without adding significant latency to the request processing pipeline.

## 12. Limitations & Future Improvements

### Current Limitations
*   **No Payment Gateway:** The purchase flow decrements inventory and issues a ticket immediately. Real-world funds transfer is currently mocked.
*   **No Frontend:** The system is purely an API; users must interact via HTTP clients like Postman.

### Future Improvements
*   **Payment Integration:** Integrate Stripe or PayPal to handle actual transactions.
*   **WebSocket Support:** Push real-time inventory updates to clients so users see "Sold Out" status without refreshing.
*   **Email Notifications:** Send the ticket UUID/QR code to the user's email upon purchase.
*   **Reporting Dashboard:** Analytics endpoints for Organizers to view sales velocity and revenue.

## 13. How to Run the Project

### Prerequisites
*   Java Development Kit (JDK) 21
*   Maven
*   Docker & Docker Compose

### Local Run (Without Docker)
1.  Ensure a local MySQL instance and Redis instance are running.
2.  Update `application.yaml` with local database credentials.
3.  Run: `mvn spring-boot:run`

### Docker Run (Recommended)
1.  Navigate to the project root directory.
2.  Build the project:
    ```bash
    mvn clean package -DskipTests
    ```
3.  Start the containers:
    ```bash
    docker-compose up --build
    ```
4.  The API will be available at `http://localhost:8080`.

### Common Startup Issues
*   **Port Conflicts:** Ensure ports 8080, 3306, and 6379 are not in use by other services.
*   **Database Connection Refused:** Ensure the MySQL container is fully healthy before the Java application attempts to connect. The configuration includes retry logic, but a slow startup may require a restart of the app container.
