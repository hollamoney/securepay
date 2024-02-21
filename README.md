# Payment Management System

**Note:** Upon running the application, a default user will be created with the following credentials:
   - Username: admin
   - Password: 123456

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
- **Swagger:** API documentation tool for easy exploration and testing.

## Getting Started

1. Clone the project:
    ```bash
    git clone https://github.com/user/payment-management-system.git
    ```
2. Navigate to the project directory:
    ```bash
    cd secure-pay
    ```
3. To start the application:
   - Using Maven Wrapper:
      ```bash
      ./mvnw spring-boot:run
      ```
   -
4. Access the application at [http://localhost:8080](http://localhost:8080).

**Note:** Upon running the application, a default user will be created with the following credentials:
   - Username: admin
   - Password: 123456

## Compatibility and Transformations

- **Java Version:** The project is compatible with Java 3 and above.
- **SwaggerConfig Incompatibility:** The project is compatible with all components except for Swagger configuration. Special configurations related to Swagger may need to be adjusted.
- **Transition from javax to Jakarta:** The project should undergo a transformation from javax packages to jakarta packages as a result of the transition in Java EE specifications. These changes, especially in dependencies and import statements, need to be made. It's essential for the project to align with the latest Java EE standards.


## Encryption Methods

The Payment Management System ensures security through the following encryption methods:

- **AES/CBC/PKCS5PADDING:** Used for encrypting credit card information.

**PCI DSS Compliance:** The system adheres to the Payment Card Industry Data Security Standard (PCI DSS) for secure payment processing.

## System Design Considerations

During the system design phase, common problems were examined, and a focus was placed on encryption for payment systems. The Payment Card Industry Data Security Standard (PCI DSS) was encountered, leading to the decision to implement encryption using the AES algorithm.

