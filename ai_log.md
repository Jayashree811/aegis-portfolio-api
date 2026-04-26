# AI Development Log

## Project: Portfolio Management API

### Technologies Used
- Java 17
- Spring Boot 3.2.4
- Spring Data JPA
- H2 Database (for local development) / PostgreSQL (production)
- Flyway (Database Migrations)
- Lombok
- OpenAPI / Swagger UI (springdoc-openapi)
- Thymeleaf (for embedded frontend)

### Core Features
- **Portfolio Management**: View holdings and manage portfolios.
- **Transactions**: Buy and sell functionality with weighted average cost calculation.
- **Dividend Tracking**: Comprehensive dividend tracking system.
- **Modern UI**: Dark-themed UI embedded directly into the backend for seamless local execution.

### Development History
- Initialized the Spring Boot application.
- Configured Maven dependencies for Spring Web, JPA, Flyway, H2, Lombok, and Testcontainers.
- Created core DTOs (`TransactionRequest` and `DividendRequest`).
- Created the main `PortfolioController`.
- Established basic database schemas with Flyway (`V1__init.sql`).
- Handled global exceptions via `GlobalExceptionHandler`.
- Added application configuration files (`application.yml` and `application-local.yml`).

## Notes
- To start the application locally, run the standard `mvn spring-boot:run` command.
- Ensure the local H2 DB is set up as per the `application-local.yml` properties.
