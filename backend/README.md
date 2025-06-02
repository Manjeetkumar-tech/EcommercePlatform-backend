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

## Running with Docker Compose

This is an alternative way to run the backend application along with its PostgreSQL database using Docker Compose. This method simplifies setup as it manages the database service for you.

### Prerequisites
*   **Docker** installed (e.g., Docker Desktop).
*   **Docker Compose** installed (usually included with Docker Desktop).

### 1. Navigate to the Backend Directory
Ensure you are in the `backend` directory where the `docker-compose.yml` file is located.

### 2. Start the Application
To build the Docker images (if not already built or if code changes were made) and start the services in detached mode (running in the background):
```bash
docker-compose up --build -d
```
If you prefer to see the logs directly in your terminal (foreground mode):
```bash
docker-compose up --build
```
The backend Spring Boot application will start, and Flyway will automatically apply database migrations.

### 3. Accessing the Application
*   The backend application will be available at `http://localhost:8080`.
*   The PostgreSQL database (managed by Docker Compose) will be accessible on port `5433` of your host machine. This can be useful for connecting with external database tools (e.g., DBeaver, pgAdmin) using connection details:
    *   Host: `localhost`
    *   Port: `5433`
    *   Database: `db1`
    *   User: `admin`
    *   Password: `password` (as defined in `docker-compose.yml`)

### 4. Stopping the Application
To stop the services and remove the containers:
```bash
docker-compose down
```

### Database Persistence
PostgreSQL data is persisted in a Docker volume named `pgdata`. This means your data will remain even if you stop and restart the services, unless the volume is explicitly removed (e.g., with `docker-compose down -v`).

### Note on `application.properties`
When running with Docker Compose, the database connection properties (`spring.datasource.url`, `spring.datasource.username`, `spring.datasource.password`) are provided by environment variables defined in the `docker-compose.yml` file. The corresponding properties in `src/main/resources/application.properties` should ideally be commented out or removed to avoid conflicts and ensure the Docker Compose environment variables take precedence.

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

## Testing Strategy

The project includes the following types of tests:

### 1. Service Layer Unit Tests
*   **Location:** `src/test/java/com/myecommerce/ecommerce_backend/services/`
*   **Description:** These tests focus on the business logic within the service classes (e.g., `ProductService`).
*   **Technology:** JUnit 5 and Mockito are used to mock repository dependencies and verify service method behavior in isolation.
*   **Example:** `ProductServiceTest.java` covers various scenarios for product creation, retrieval, updates, and deletion.

### 2. Controller Layer Unit Tests
*   **Location:** `src/test/java/com/myecommerce/ecommerce_backend/controllers/`
*   **Description:** These tests verify the behavior of the Spring MVC controllers, ensuring correct handling of web requests, request/response serialization, parameter binding, and interaction with mocked service layers.
*   **Technology:**
    *   Spring MVC Test Framework (`MockMvc`)
    *   `@WebMvcTest` annotation (isolates the web layer)
    *   Mockito (`@MockBean` for service dependencies)
    *   `spring-security-test` for testing secured endpoints (CSRF token handling, `@WithMockUser`).
*   **Examples:**
    *   `ProductControllerTest.java`: Tests product API endpoints.
    *   `UserControllerTest.java`: Tests user registration and login API endpoints.
*   **Key Aspects Tested:** Request mappings, status codes, JSON request/response processing, basic validation, and security integration at the controller level.

### Future Enhancements
*   **Integration Tests:** To test the full request lifecycle from controller to database.
*   **More Comprehensive Validation Tests:** Detailed tests for various input validation scenarios.

## Running Tests
To run the unit tests:
```bash
./mvnw test
```
