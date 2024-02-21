# Payment Management System

## Project Overview

The Payment Management System is a Java Spring Boot application designed to facilitate secure and efficient payment management. The system securely processes payment transactions, allowing users to track their payments.

## Technology Stack

- **Java Spring Boot:** Backend application development.
- **Spring Data JPA:** Database operations.
- **Spring Security:** Security features.
- **Hibernate:** Interaction with the database.
- **RESTful API:** APIs for managing user payment transactions.
- **H2 Database (Optional):** Lightweight, in-memory database for development and testing.
- **Maven:** Dependency management and project compilation.

## Getting Started

1. Clone the project:
    ```bash
    git clone https://github.com/user/payment-management-system.git
    ```
2. Navigate to the project directory:
    ```bash
    cd payment-management-system
    ```
3. To start the application:
   - Using Maven Wrapper:
      ```bash
      ./mvnw spring-boot:run
      ```
4. Access the application at [http://localhost:8080](http://localhost:8080).

## Encryption Methods

The Payment Management System ensures security through the following encryption methods:

- **AES/CBC/PKCS5PADDING:** Used for encrypting credit card information.

