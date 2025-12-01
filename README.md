# Flight Booking Microservices Application

## Project Overview

This project is a comprehensive Flight Booking System built using a Microservices Architecture with Java and Spring Boot. It demonstrates the implementation of modern distributed system patterns including Service Discovery, API Gateway Routing, Circuit Breaking, Asynchronous Messaging, and Reactive Programming.

The system allows users to search for flights, book tickets, and receive asynchronous email confirmations upon successful booking. It is designed to be resilient, scalable, and fault-tolerant.

## Architecture & Technologies

### Architecture Patterns Used
* **Microservices:** Decomposed business logic into separate, loosely coupled services.
* **API Gateway:** Centralized entry point for routing, load balancing, and filtering.
* **Service Discovery:** Dynamic registration and lookup of services using Netflix Eureka.
* **Circuit Breaker:** Fault tolerance using Resilience4j to handle downstream failures gracefully.
* **Asynchronous Messaging:** Decoupled email notifications using RabbitMQ.
* **Reactive Programming:** Non-blocking I/O using Spring WebFlux (Project Reactor).

### Technology Stack
* **Programming Language:** Java 21
* **Framework:** Spring Boot 3.x / 4.0
* **Database:** MongoDB (Reactive Driver)
* **Message Broker:** RabbitMQ
* **Containerization:** Docker (for RabbitMQ)
* **Build Tool:** Maven

### Core Dependencies
* Spring Cloud Gateway
* Spring Cloud Netflix Eureka
* Spring Cloud Circuit Breaker (Resilience4j)
* Spring Cloud LoadBalancer
* Spring Boot Starter AMQP (RabbitMQ)
* Spring Boot Starter Mail
* Spring Boot Starter WebFlux & Web

## Microservices Breakdown

| Service Name | Port | Description |
| :--- | :--- | :--- |
| **eureka-server** | 8761 | Service Registry. All microservices register here to be discoverable. |
| **api-gateway** | 8765 | Entry point for all client requests. Handles routing, load balancing, and circuit breaking. |
| **flight-service** | 8081 | Manages flight inventory, airlines, and search operations. Built on a Reactive stack. |
| **booking-service** | 8082 | Handles ticket booking logic, passenger details, and produces messages to RabbitMQ. |
| **notification-service** | 8083 | Consumer service that listens to RabbitMQ and sends email confirmations via Gmail SMTP. |
| **config-server** | 8888 | Centralized configuration server (Optional/Configurable). |

## Setup & Installation

### Prerequisites
1.  Java Development Kit (JDK) 21 installed.
2.  Maven installed.
3.  MongoDB running locally on port `27017`.
4.  Docker Desktop installed and running (for RabbitMQ).

### Step 1: Start Infrastructure
Navigate to the root directory and start the RabbitMQ container using Docker Compose:
docker compose up -d

Verify RabbitMQ is running at `http://localhost:15672` (Username/Password: `guest`/`guest`).

### Step 2: Configuration
Update the `application.properties` or `application.yml` in the **notification-service** with your email credentials.

* **File:** `notification-service/src/main/resources/application.properties`
* **Properties:**
    * `spring.mail.username`: Your Gmail address.
    * `spring.mail.password`: Your Google App Password (16-character code).

### Step 3: Run the Microservices
Start the services in the following strict order to ensure proper registration:

1.  **Eureka Server** (`eureka-server`)
2.  **Flight Service** (`flight-service`)
3.  **Booking Service** (`booking-service`)
4.  **Notification Service** (`notification-service`)
5.  **API Gateway** (`api-gateway`)

Ensure all services are up and registered on the Eureka Dashboard at `http://localhost:8761`.

## API Documentation

All API requests should be routed through the API Gateway running on port **8765**.

### 1. Search Flights
* **Endpoint:** `POST http://localhost:8765/api/flight/search`
* **Description:** Searches for available flights based on criteria.
* **Request Body:**
    ```json
    {
        "departingAirport": "DEL",
        "arrivalAirport": "BLR",
        "departDate": "2025-12-05",
        "passengers": {
            "adults": 1,
            "children": 0
        }
    }
    ```

### 2. Book Flight
* **Endpoint:** `POST http://localhost:8765/api/flight/booking/{flightNumber}`
* **Description:** Books a flight, saves passenger details, and triggers an email notification.
* **Path Variable:** `flightNumber` (e.g., `SG902`)
* **Request Body:**
    ```json
    {
        "emailId": "user@example.com",
        "contactNumber": "9876543210",
        "numberOfSeats": 1,
        "passengers": [
            {
                "passengerName": "John Doe",
                "age": 30,
                "gender": "Male",
                "seatNum": "A1",
                "mealPref": "Veg"
            }
        ]
    }
    ```
* **Response (201 Created):**
    ```json
    {
        "pnr": "S68BFF",
        "totalPrice": 5400.0,
        "message": "Booking successful",
        "emailId": "user@example.com",
        "passengerName": "John Doe",
        "flightNumber": "SG902"
    }
    ```

### 3. Get Ticket Details
* **Endpoint:** `GET http://localhost:8765/api/flight/ticket/{pnr}`
* **Description:** Retrieves booking details using the PNR.

### 4. Admin - Add Inventory (Direct Access)
* **Endpoint:** `POST http://localhost:8081/api/flight/airline/inventory/add`
* **Description:** Adds new flight schedules to the database.

## Key Features Implementation Details

### Service Discovery (Eureka)
* All services act as Eureka Clients (`eureka.client.register-with-eureka=true`).
* The API Gateway uses `lb://SERVICE-NAME` to dynamically resolve service IP addresses, allowing for horizontal scaling without changing code.

### API Gateway & Routing
* Configured in `GatewayConfig.java`.
* Routes are ordered by specificity (Booking Service specific routes are processed before generic Flight Service routes).
* Implements `Resilience4j` circuit breakers on routes to prevent cascading failures.

### Circuit Breaker (Resilience4j)
* **Implementation:** Applied at the API Gateway level.
* **Behavior:** If `flight-service` or `booking-service` becomes unresponsive or throws errors (50% failure rate threshold), the circuit opens.
* **Fallback:** Requests are forwarded to a `FallbackController` which returns a user-friendly message (e.g., "Flight Service is currently unavailable") instead of a system error.

### Asynchronous Notifications (RabbitMQ)
1.  **Producer (Booking Service):** Upon successful booking, a message containing PNR, Email, and Name is pushed to the `email_queue`.
2.  **Consumer (Notification Service):** Listens to `email_queue`. When a message is received, it triggers `JavaMailSender` to send a formatted email confirmation to the user.
3.  **Benefit:** Decouples the booking process from the email sending process, ensuring high performance for the user.

```bash
docker compose up -d
