# Grambasket Microservices

This repository contains the backend source code for the Grambasket application, a modern e-commerce platform built using a distributed microservices architecture.

## Architecture Overview

The application follows a microservices pattern to ensure scalability, resilience, and maintainability. Each core business functionality is encapsulated within its own service.

The primary components established so far are:

*   **Discovery Server (`discovery-server`):** Utilizes Netflix Eureka to provide a dynamic service registry. All other microservices register with Eureka, allowing them to locate and communicate with each other without hardcoded addresses.
*   **API Gateway (`gateway-service`):** Acts as the single, unified entry point for all external client requests. It uses Spring Cloud Gateway to route traffic to the appropriate downstream service and is the ideal place to handle cross-cutting concerns like security, rate limiting, and logging.
*   **Authentication Service (`auth-service`):** A dedicated service responsible for all userProfile identity and access management. It handles userProfile registration, login, and the issuance and validation of JSON Web Tokens (JWT).

## Technology Stack

*   **Language:** Java 17
*   **Framework:** Spring Boot & Spring Cloud
*   **Service Discovery:** Netflix Eureka
*   **API Gateway:** Spring Cloud Gateway
*   **Authentication:** Spring Security with JWT
*   **Database:** MongoDB (for the `auth-service`)
*   **Build Tool:** Apache Maven

## Modules

### `discovery-server`

*   **Purpose:** The service registry for the entire microservices ecosystem.
*   **Port:** `8761`

### `gateway-service`

*   **Purpose:** The main entry point for all API requests. It intelligently routes traffic to internal services. For example, it forwards requests from `/grambasket/api/auth-service/**` to the `auth-service`.
*   **Port:** `8082` (or as configured)

### `auth-service`

*   **Purpose:** Manages userProfile accounts and authentication.
*   **Port:** `8083` (or as configured)
*   **API Endpoints:**
    *   `POST /api/auth-service/register`: Creates a new userProfile account.
    *   `POST /api/auth-service/login`: Authenticates a userProfile and returns JWT access and refresh tokens.
    *   `POST /api/auth-service/refresh`: Generates a new access token using a valid refresh token.
    *   `POST /api/auth-service/logout`: Invalidates a userProfile's session.

## How to Run

### Prerequisites

*   JDK 17 or later
*   Apache Maven
*   A running instance of MongoDB

### Build

Navigate to the project's root directory (`D:/Grambasket/grambasket/`) and execute the following Maven command to build all modules:


### Execution Order

For the system to function correctly, the services **must** be started in the following sequence:

1.  **`discovery-server`**
2.  **`auth-service`**
3.  **`gateway-service`**

You can run each service by navigating to its `target` directory and executing the generated JAR file. For example, to start the discovery server:
