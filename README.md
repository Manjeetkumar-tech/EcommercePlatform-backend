# Ecommerce Platform Backend

## Description
This project is the backend for a niche-focused e-commerce platform, built with Spring Boot. It provides RESTful APIs for managing users, products, and eventually orders and other e-commerce functionalities.

## Features Implemented
*   **User Management:**
    *   User registration (`/api/users/register`)
    *   User login (`/api/users/login`) - Establishes authentication.
    *   Secure logout (`/api/logout`) - Invalidates session and clears security context.
*   **Product Catalog Management:**
    *   Add new products (Admin only)
    *   View all products (Public)
    *   View a specific product by ID (Public)
    *   Update existing products (Admin only)
    *   Delete products (Admin only)
*   **Security:**
    *   Role-Based Access Control (RBAC): Differentiates between `USER` and `ADMIN` roles.
    *   CSRF (Cross-Site Request Forgery) Protection: Enabled for all state-changing requests.
    *   Secure endpoints with appropriate authentication and authorization rules.
    *   Input validation for product data.
    *   HTTP Basic Authentication enabled.

## Technologies Used
*   **Java 17**
*   **Spring Boot 3.x**
*   **Spring Security 6.x** (for authentication, authorization, CSRF)
*   **Spring Data JPA** (for database interaction)
*   **Hibernate** (as JPA provider)
*   **PostgreSQL** (as the database)
*   **Maven** (for dependency management and build)
*   **JUnit 5 & Mockito** (for unit testing)
*   **Docker** (Dockerfile included for containerization)

## Prerequisites
*   **JDK 17** or later (e.g., OpenJDK, Oracle JDK)
*   **Maven 3.6.x** or later (or use the included Maven Wrapper `mvnw`)
*   **PostgreSQL** database server running.

## Getting Started

### 1. Clone the Repository
```bash
git clone <repository_url>
cd ecommerce-backend 
```
*(Replace `<repository_url>` with the actual URL of your Git repository)*

### 2. Configure the Database
*   Open the `src/main/resources/application.properties` file.
*   Update the following properties to match your PostgreSQL setup:
    ```properties
    spring.datasource.url=jdbc:postgresql://localhost:5432/db1
    spring.datasource.username=your_postgres_username
    spring.datasource.password=your_postgres_password
    ```
    *   Ensure the database `db1` (or your chosen name) exists in your PostgreSQL instance. Spring Boot can create/update tables based on `spring.jpa.hibernate.ddl-auto=update`, but the database itself must exist.

### 3. Build the Project
You can use the Maven wrapper included with the project:
```bash
./mvnw clean install
```
(On Windows, use `mvnw.cmd clean install`)

### 4. Run the Application
```bash
./mvnw spring-boot:run
```
Alternatively, you can run the packaged JAR file from the `target/` directory:
```bash
java -jar target/ecommerce-backend-0.0.1-SNAPSHOT.jar
```
The application will typically start on `http://localhost:8080`.

## API Endpoints Overview

### User Authentication
*   `POST /api/users/register`: Register a new user.
    *   Request Body: User details (e.g., `{"username": "newuser", "email": "newuser@example.com", "password": "password123"}`)
*   `POST /api/users/login`: Login an existing user.
    *   Request Body: Login credentials (e.g., `{"email": "user@example.com", "password": "password"}`)
    *   Authenticates the user and establishes a session.
*   `POST /api/logout`: Logout the currently authenticated user.
    *   Invalidates session and clears security tokens.

### Product Management
*   `GET /api/products`: Get a list of all products. (Public)
*   `GET /api/products/{id}`: Get a specific product by its ID. (Public)
*   `POST /api/products`: Add a new product. (Admin only)
    *   Requires Authentication (Admin role) and CSRF token.
    *   Request Body: Product details.
*   `PUT /api/products/{id}`: Update an existing product. (Admin only)
    *   Requires Authentication (Admin role) and CSRF token.
    *   Request Body: Product details.
*   `DELETE /api/products/{id}`: Delete a product. (Admin only)
    *   Requires Authentication (Admin role) and CSRF token.

### Authentication and Authorization
*   The application uses HTTP Basic authentication.
    *   Default admin credentials (for testing): `username: admin`, `password: adminpassword`
*   Endpoints are secured based on roles (`ADMIN`, `USER`).

## Security Notes

### CSRF Protection
*   CSRF protection is **enabled**.
*   For state-changing requests (POST, PUT, DELETE), clients must include a valid CSRF token.
*   The backend provides the CSRF token in a cookie named `XSRF-TOKEN` (non-HttpOnly).
*   Clients should read this cookie and send its value in an HTTP header named `X-CSRF-TOKEN`.

### Role-Based Access Control (RBAC)
*   **ADMIN Role**: Can manage products (add, update, delete) and access all authenticated user endpoints.
*   **USER Role**: Can access general authenticated endpoints (once more are added, e.g., view own profile, manage own orders). Currently, can access product viewing endpoints (which are public) and authenticated-only endpoints not restricted to ADMIN.

## Database
*   The application is configured to use PostgreSQL.
*   Database connection properties are in `src/main/resources/application.properties`.
*   JPA's `ddl-auto` is set to `update`, meaning Hibernate will attempt to update the schema based on entity definitions. For production, consider using database migration tools like Flyway or Liquibase.

## Running Tests
To run the unit tests:
```bash
./mvnw test
```
