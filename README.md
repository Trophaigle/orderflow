# OrderFlow — Order Management REST API

## 1. Overview

**OrderFlow** is a REST API built with Java and Spring Boot to manage products, inventory, and customer orders.

The project is a hands-on learning project covering backend development, REST APIs, database persistence, automated testing, and Spring Boot architecture.

It provides endpoints to manage products, create orders, calculate order totals, and update product inventory when an order is placed.

The application can run with either an in-memory H2 database or a PostgreSQL database. This allows lightweight development with H2 and persistent storage with PostgreSQL.

## 2. Tech Stack

| Technology | Purpose |
|---|---|
| Java 21 | Programming language |
| Spring Boot | Application framework |
| Spring Web | REST endpoints and HTTP handling |
| Spring Data JPA | Database access through repositories |
| Hibernate | ORM implementation used by JPA |
| H2 | In-memory database for development and testing |
| PostgreSQL | Persistent relational database |
| Maven | Dependency management, build, and test execution |
| JUnit 5 | Automated testing framework |
| Mockito | Mocking dependencies in unit tests |
| Git and GitHub | Version control and remote repository |

## 3. Architecture

OrderFlow follows a layered architecture. Each layer has a specific responsibility.

```text
HTTP Client
    |
    v
Controller Layer
    |
    v
Service Layer
    |
    v
Repository Layer
    |
    v
JPA / Hibernate
    |
    v
H2 or PostgreSQL
```

### Controller layer

Controllers expose the REST API and handle HTTP requests and responses.

Examples:
- `ProductController`
- `OrderController`

They define endpoints, receive request data, call the relevant services, and return HTTP responses.

### Service layer

Services contain the business logic.

Examples:
- `ProductService`
- `OrderService`

Responsibilities include validating product data, checking that products exist, calculating order totals, and ensuring that sufficient stock is available.

Keeping business logic in services avoids putting all application logic inside controllers.

### Repository layer

Repositories provide database access through Spring Data JPA.

Examples:
- `ProductRepository`
- `OrderRepository`
- `OrderItemRepository`

Repositories extend `JpaRepository`, which provides common database operations such as saving, finding, and deleting entities without requiring handwritten SQL for basic operations.

### Entity layer

Entities represent objects persisted in the relational database.

- `Product`: product name, price, stock, and identifier.
- `Order`: order total and associated order items.
- `OrderItem`: product and quantity for an individual order line.

JPA annotations define how these objects map to database tables and relationships.

### Dependency injection

Spring manages application components and their dependencies.

For example, `OrderService` receives its repositories through constructor injection rather than creating repository instances itself. This makes dependencies explicit and services easier to test with Mockito.

## 4. Project Structure

The main source files are organized by feature.

```text
orderflow/
├── src/
│   ├── main/
│   │   ├── java/com/example/orderflow/
│   │   │   ├── product/
│   │   │   │   ├── Product.java
│   │   │   │   ├── ProductController.java
│   │   │   │   ├── ProductService.java
│   │   │   │   └── ProductRepository.java
│   │   │   └── order/
│   │   │       ├── Order.java
│   │   │       ├── OrderItem.java
│   │   │       ├── OrderController.java
│   │   │       ├── OrderService.java
│   │   │       ├── OrderRepository.java
│   │   │       ├── OrderItemRepository.java
│   │   │       └── OrderLineRequest.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── application-postgres.properties
│   └── test/
│       └── java/com/example/orderflow/
├── pom.xml
└── README.md
```

The test directory contains automated tests for the application's controllers and services.

## 5. Main Features

### Product management

The product API supports:
- Creating products.
- Retrieving products.
- Retrieving a product by its identifier.
- Updating product information.
- Deleting products.
- Validating product data.
- Returning an HTTP 404 response when a product does not exist.

Product prices use `BigDecimal` rather than `double` to represent monetary values more appropriately.

Stock cannot be negative, and prices must be positive.

### Order management

The order API supports:
- Creating an order containing one or more items.
- Retrieving all orders.
- Retrieving an order by its identifier.
- Calculating the order total from product prices and quantities.
- Checking product availability before accepting an order.
- Decreasing product stock when an order is created.
- Returning an HTTP 409 Conflict response when stock is insufficient.

An order contains multiple order items. Each order item references a product and records the requested quantity.

The total is calculated as:

```text
Order total = Sum of (product price × item quantity)
```

For example, two keyboards priced at €79.90 each produce an order total of €159.80.

## 6. REST API Endpoints

The base URL is:

```text
http://localhost:8080
```

### Products

| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/api/products` | Retrieve all products |
| GET | `/api/products/{id}` | Retrieve a product by ID |
| POST | `/api/products` | Create a product |
| PUT | `/api/products/{id}` | Update a product |
| DELETE | `/api/products/{id}` | Delete a product |

### Orders

| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/api/orders` | Retrieve all orders |
| GET | `/api/orders/{id}` | Retrieve an order by ID |
| POST | `/api/orders` | Create an order |

### Example: Create a product

Request:

```http
POST /api/products
Content-Type: application/json
```

JSON body:

```json
{
  "name": "Keyboard",
  "price": 79.90,
  "stock": 10
}
```

### Example: Create an order

Request:

```http
POST /api/orders
Content-Type: application/json
```

JSON body:

```json
{
  "items": [
    {
      "productId": 1,
      "quantity": 2
    }
  ]
}
```

If product `1` costs €79.90 and has sufficient stock, the expected order total is €159.80, and its stock decreases by two units.

A successful order creation returns HTTP `201 Created`.

## 7. Running the Application

### Prerequisites

Install and configure:
- JDK 21.
- Maven.
- Git.
- PostgreSQL 18 if you want to use PostgreSQL.
- An HTTP client, such as a browser for GET requests or PowerShell for requests that create or update data.

The following commands use Windows PowerShell.

Open a terminal in the project root directory:

```powershell
cd E:\JavaSpringBoot\orderflow
```

### Option A: Run with H2

The default configuration is stored in:

```text
src/main/resources/application.properties
```

It uses an in-memory H2 database.

Start the application:

```powershell
mvn spring-boot:run
```

Spring Boot uses the default configuration because the PostgreSQL profile has not been activated.

Check the API:

```text
http://localhost:8080/api/products
```

A newly initialized database should return an empty JSON array:

```json
[]
```

**Important:** the H2 database uses `jdbc:h2:mem:orderflow`. Its data is held in memory and is normally lost when the application stops. Restarting the application creates a fresh in-memory database.

The H2 dependency and configuration are retained so that H2 can still be used independently of PostgreSQL.

### Option B: Run with PostgreSQL

The PostgreSQL configuration is stored in:

```text
src/main/resources/application-postgres.properties
```

It contains the following connection settings:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/orderflow
spring.datasource.username=orderflow_user
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.hibernate.ddl-auto=update
spring.h2.console.enabled=false
```

The PostgreSQL server must be running, and the database `orderflow` and login role `orderflow_user` must already exist.

#### 1. Set the database password

In PowerShell, set the environment variable to the password configured for the PostgreSQL login role:

```powershell
$env:DB_PASSWORD = "YOUR_LOCAL_DATABASE_PASSWORD"
```

Replace the placeholder with your actual local password. Do not commit the password to Git.

The variable applies to the current PowerShell session. If you open a new terminal, set it again before launching the application.

#### 2. Start Spring Boot with the PostgreSQL profile

```powershell
mvn spring-boot:run "-Dspring-boot.run.profiles=postgres"
```

This activates `application-postgres.properties` instead of relying only on the default H2 configuration.

#### 3. Verify the connection

Open:

```text
http://localhost:8080/api/products
```

The response should list products stored in PostgreSQL. If the database has no products, it should return `[]`.

#### 4. Verify persistence

Create a product, stop the application with `Ctrl+C`, and restart it using the PostgreSQL command.

The product should still exist after the restart because PostgreSQL stores data persistently.

You can also inspect the tables and their contents in pgAdmin 4.

### Important database configuration note

The setting:

```properties
spring.jpa.hibernate.ddl-auto=update
```

allows Hibernate to update the database schema to match the application's entities.

This is convenient for a learning project. For a production application, a migration tool such as Flyway or Liquibase is generally preferable for controlled, versioned schema changes.

## 8. Running Automated Tests

Run the full test suite:

```powershell
mvn test
```

A successful run ends with:

```text
BUILD SUCCESS
```

To package the application and run the tests as part of the build:

```powershell
mvn package
```

### JUnit

JUnit is the test framework used to define and execute automated tests.

Tests can verify expected behavior, such as:
- Calculating an order total correctly.
- Creating an order with the expected number of items.
- Validating product data.
- Handling invalid input and missing products.

### Mockito

Mockito allows unit tests to replace dependencies with mocks.

For example, a service test can mock `ProductRepository` and configure its behavior without requiring a real database.

Typical Mockito operations include:

```java
when(productRepository.findById(1L))
    .thenReturn(Optional.of(product));

when(orderRepository.save(any(Order.class)))
    .thenAnswer(invocation -> invocation.getArgument(0));
```

These examples configure the mock repository's behavior. The actual test assertions then check the result produced by the service.

### Why use mocks?

A unit test should ideally test the behavior of one component in isolation.

Mocking the repository allows `OrderService` to be tested without depending on PostgreSQL availability, database contents, or a running server.

Mocks do not prove that the complete application works with a real database. Database integration tests can complement unit tests.

## 9. Calling the API from PowerShell

PowerShell provides a simple way to send HTTP requests without installing another client.

Keep the Spring Boot application running in one terminal. Run these commands in a second PowerShell terminal.

### Get all products

```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/products"
```

### Create a product

```powershell
$body = @{
    name = "Keyboard"
    price = 79.90
    stock = 10
} | ConvertTo-Json

Invoke-RestMethod `
    -Uri "http://localhost:8080/api/products" `
    -Method Post `
    -ContentType "application/json" `
    -Body $body
```

### Get a product by ID

```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/products/1"
```

### Create an order

```powershell
$body = @{
    items = @(
        @{
            productId = 1
            quantity = 2
        }
    )
} | ConvertTo-Json -Depth 5

Invoke-RestMethod `
    -Uri "http://localhost:8080/api/orders" `
    -Method Post `
    -ContentType "application/json" `
    -Body $body
```

### Get all orders

```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/orders"
```

These examples work with whichever database profile is currently active. The data is stored in that selected database.

## 10. Calling the API from a React Frontend

A frontend application can communicate with OrderFlow using HTTP requests. React's `fetch` API is one way to do this.

### Retrieve products

```javascript
async function getProducts() {
  const response = await fetch("http://localhost:8080/api/products");

  if (!response.ok) {
    throw new Error(`HTTP error: ${response.status}`);
  }

  return await response.json();
}
```

### Create a product

```javascript
async function createProduct() {
  const response = await fetch("http://localhost:8080/api/products", {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify({
      name: "Keyboard",
      price: 79.90,
      stock: 10
    })
  });

  if (!response.ok) {
    throw new Error(`HTTP error: ${response.status}`);
  }

  return await response.json();
}
```

### Create an order

```javascript
async function createOrder() {
  const response = await fetch("http://localhost:8080/api/orders", {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify({
      items: [
        {
          productId: 1,
          quantity: 2
        }
      ]
    })
  });

  if (!response.ok) {
    throw new Error(`HTTP error: ${response.status}`);
  }

  return await response.json();
}
```

These are frontend examples, not part of the current Spring Boot backend.

**CORS note:** if a React application runs on a different origin, such as `http://localhost:3000` or `http://localhost:5173`, the backend may need an appropriate CORS configuration before the browser permits the requests.

## 11. HTTP Status Codes and Error Handling

The API uses HTTP status codes to communicate outcomes.

| Status | Meaning |
|---|---|
| `200 OK` | A request completed successfully |
| `201 Created` | A resource, such as an order, was created |
| `404 Not Found` | The requested resource does not exist |
| `409 Conflict` | The requested operation conflicts with the current state, such as insufficient stock |
| `400 Bad Request` | Invalid request data, when handled by the relevant validation or exception handling |

The exact response body for an error depends on the controller, validation, and exception-handling configuration.

Using appropriate status codes allows a frontend to distinguish successful requests from validation errors and business conflicts.

## 12. Important Concepts Learned

### REST APIs

HTTP methods communicate the operation being requested:
- `GET` retrieves resources.
- `POST` creates resources.
- `PUT` updates resources.
- `DELETE` removes resources.

JSON is used to exchange data between the client and the backend.

### Dependency injection

Spring creates and manages application components and injects their dependencies. Constructor injection makes those dependencies explicit and simplifies testing.

### JPA and Hibernate

JPA defines the object-relational persistence API, while Hibernate implements it in this project.

Entities are mapped to relational tables, and repositories provide data-access operations.

### Relational database relationships

An order can contain multiple order items. Each order item references a product and records a quantity.

These relationships are represented using JPA mappings such as `@OneToMany` and `@ManyToOne`.

### Business validation

The application validates product data and checks inventory before creating orders.

The distinction between request validation and business validation is important:
- Request validation checks whether incoming data meets defined constraints.
- Business validation checks whether an operation makes sense given the application's current state.

### Unit testing and mocking

JUnit executes tests, while Mockito provides mocks for dependencies.

This allows business logic to be tested without requiring a real database for every test.

### Database profiles

Spring profiles allow different configurations to be selected without changing the Java business logic.

In this project:
- The default configuration uses H2.
- The `postgres` profile uses PostgreSQL.

### Persistence

In-memory H2 data normally disappears when the application stops. PostgreSQL data persists independently of the Spring Boot process.

### Version control

Git tracks source-code changes, while GitHub hosts the remote repository. Commits preserve the history of changes and make it possible to revisit earlier versions.

## 13. Potential Improvements

OrderFlow is a learning project and can be extended in several directions:

- Add transactional order creation so a failed order cannot leave inventory partially updated.
- Add integration tests using a real or containerized database.
- Improve centralized exception handling with `@ControllerAdvice`.
- Introduce DTOs to separate the external API representation from persistence entities.
- Add database schema migrations with Flyway or Liquibase.
- Document the API using OpenAPI/Swagger.
- Add pagination and filtering to product and order endpoints.
- Add authentication and authorization.
- Build a React frontend.
- Add a CI pipeline to run automated tests on every push.

## 14. Running OrderFlow: Quick Reference

| Task | Command |
|---|---|
| Start with H2 | `mvn spring-boot:run` |
| Start with PostgreSQL | `mvn spring-boot:run "-Dspring-boot.run.profiles=postgres"` |
| Run tests | `mvn test` |
| Build and test | `mvn package` |
| Check all products | `GET http://localhost:8080/api/products` |
| Check all orders | `GET http://localhost:8080/api/orders` |

For PostgreSQL, set the `DB_PASSWORD` environment variable in the current PowerShell session before starting the application.

## 15. Summary

OrderFlow demonstrates the fundamentals of building a Java backend with Spring Boot.

It combines a layered architecture, REST endpoints, business services, Spring Data JPA repositories, relational entities, two database configurations, inventory management, order-total calculation, and automated tests using JUnit and Mockito.

The project provides a foundation for exploring more advanced backend topics such as transaction management, integration testing, API documentation, database migrations, security, and frontend integration.
'''