# Spring Boot Task Management API

A secure REST API built with **Java 21, Spring Boot 3, Spring Data JPA, Spring Security, JWT, and MySQL**. This project implements user authentication, role-based authorization, task CRUD operations, category management, pagination, filtering, validation, and exception handling.

## Features

* **JWT Authentication:** User registration, login, and access/refresh token management.
* **Password Security:** Password hashing using BCrypt.
* **Role-Based Authorization:** Supports `ROLE_USER` and `ROLE_ADMIN`.
* **Task CRUD:** Create, retrieve, update, and delete tasks.
* **Task Ownership:** Regular users can manage their own tasks.
* **Category Management:** Retrieve categories; create, update, and delete categories as an administrator.
* **Pagination:** Retrieve tasks page by page.
* **Status Filtering:** Filter tasks by status.
* **Input Validation:** Request validation using Bean Validation.
* **Exception Handling:** Handles invalid requests, authentication failures, authorization errors, and missing resources.
* **Database Migrations:** Flyway-managed database schema.
* **API Testing:** Endpoints tested using Postman.

## Technology Stack

| Technology      | Purpose                              |
| --------------- | ------------------------------------ |
| Java 21         | Programming language                 |
| Spring Boot 3   | Application framework                |
| Spring Web      | REST API development                 |
| Spring Data JPA | Database persistence                 |
| Spring Security | Authentication and authorization     |
| JWT             | Stateless token-based authentication |
| BCrypt          | Password hashing                     |
| MySQL           | Relational database                  |
| Flyway          | Database migrations                  |
| Maven           | Build and dependency management      |
| Postman         | API testing                          |

## Prerequisites

Install the following:

* JDK 21
* MySQL Server
* Git (optional)
* Postman (recommended)

The project includes the Maven Wrapper, so a separate Maven installation is not required.

## Getting Started

### 1. Clone the repository

```bash
git clone <YOUR_GITHUB_REPOSITORY_URL>
cd task-management-api
```

Replace `<YOUR_GITHUB_REPOSITORY_URL>` with this repository's actual URL.

Alternatively, download and extract the repository ZIP.

### 2. Create the database

Start MySQL and execute:

```sql
CREATE DATABASE task_management;
```

The application connects to the `task_management` database.

### 3. Configure environment variables

The application reads the database password and JWT signing secret from environment variables.

| Variable      | Description                          |
| ------------- | ------------------------------------ |
| `DB_PASSWORD` | Password for the MySQL database user |
| `JWT_SECRET`  | Secret used to sign JWT tokens       |

Check `src/main/resources/application.properties` for the database URL, username, port, and other application settings. Ensure they match your local environment.

**Windows PowerShell:**

```powershell
$env:DB_PASSWORD = Read-Host "Enter MySQL password"
$env:JWT_SECRET = Read-Host "Enter JWT secret"
```

Enter your actual values when prompted. Do not commit real credentials or JWT secrets to GitHub.

### 4. Run the application

With the environment variables configured in the same terminal, run:

```powershell
.\mvnw.cmd spring-boot:run
```

The application uses the configured server port. With the default Spring Boot port, the base URL is:

`http://localhost:8080`

Flyway applies the database migrations when the application starts.

### 5. Build and test

Run the tests:

```powershell
.\mvnw.cmd clean test
```

Package the application:

```powershell
.\mvnw.cmd clean package
```

The generated JAR is placed in the `target` directory.

## API Reference

Base URL:

`http://localhost:8080`

### Authentication

| Method | Endpoint             | Description                   | Expected response                  |
| ------ | -------------------- | ----------------------------- | ---------------------------------- |
| POST   | `/api/auth/register` | Register a user               | `201 Created`                      |
| POST   | `/api/auth/login`    | Authenticate a user           | `200 OK`                           |
| POST   | `/api/auth/refresh`  | Refresh authentication tokens | `200 OK` for a valid refresh token |

#### Register a user

**Request**

`POST /api/auth/register`

```json
{
  "username": "testuser",
  "email": "testuser@example.com",
  "password": "TestPass123!"
}
```

A newly registered user receives the `ROLE_USER` role by default.

#### Log in

**Request**

`POST /api/auth/login`

```json
{
  "email": "testuser@example.com",
  "password": "TestPass123!"
}
```

A successful login returns an access token and a refresh token.

**Example response structure:**

```json
{
  "accessToken": "<access-token>",
  "refreshToken": "<refresh-token>",
  "tokenType": "Bearer"
}
```

The values above are placeholders. Actual tokens are generated at login.

#### Authenticate protected requests

In Postman:

1. Open the request's **Authorization** tab.
2. Select **Bearer Token**.
3. Paste the `accessToken` returned by login into the Token field.
4. Send the request.

Use the **access token**, not the refresh token, for protected API endpoints.

#### Refresh tokens

**Request**

`POST /api/auth/refresh`

```json
{
  "refreshToken": "<your-refresh-token>"
}
```

A valid refresh token returns new authentication tokens.

Expected behavior:

| Scenario                               | Expected response                                |
| -------------------------------------- | ------------------------------------------------ |
| Valid refresh token                    | `200 OK`                                         |
| Access token supplied as refresh token | `401 Unauthorized`                               |
| Invalid or expired refresh token       | `401 Unauthorized`                               |
| Missing refresh token                  | `400 Bad Request`, subject to request validation |

## Task Management

All task endpoints require a valid access token.

| Method | Endpoint          | Description           |
| ------ | ----------------- | --------------------- |
| POST   | `/api/tasks`      | Create a task         |
| GET    | `/api/tasks`      | List your tasks       |
| GET    | `/api/tasks/{id}` | Retrieve a task by ID |
| PUT    | `/api/tasks/{id}` | Update a task         |
| DELETE | `/api/tasks/{id}` | Delete a task         |

### Create a task

**Request**

`POST /api/tasks`

```json
{
  "title": "Complete Java assignment",
  "description": "Finish the Task Management API",
  "dueDate": "2026-10-29",
  "priority": "HIGH",
  "status": "TODO",
  "categoryId": null
}
```

A successful creation returns `201 Created`.

The `categoryId` can be set to an existing category ID when assigning a category. The example uses `null`.

### List tasks

`GET /api/tasks`

Returns the authenticated user's tasks.

### Filter tasks by status

`GET /api/tasks?status=TODO`

Supported statuses:

* `TODO`
* `IN_PROGRESS`
* `COMPLETED`

### Pagination

Retrieve the first page with two tasks:

`GET /api/tasks?page=0&size=2`

Retrieve the next page:

`GET /api/tasks?page=1&size=2`

Combine pagination and filtering:

`GET /api/tasks?status=TODO&page=0&size=2`

The paginated response includes the task content and page metadata.

### Retrieve a task

`GET /api/tasks/1`

Replace `1` with an existing task ID.

### Update a task

**Request**

`PUT /api/tasks/1`

```json
{
  "title": "Complete and test Java assignment",
  "description": "Finish the core API endpoints",
  "dueDate": "2026-10-29",
  "priority": "MEDIUM",
  "status": "IN_PROGRESS",
  "categoryId": null
}
```

Replace the task ID with the ID you want to update.

### Delete a task

`DELETE /api/tasks/1`

Replace `1` with the task ID.

A successful deletion returns `204 No Content`.

Supported priorities:

* `LOW`
* `MEDIUM`
* `HIGH`

### Task authorization

Regular users can manage their own tasks. Access to another user's task is restricted.

## Category Management

| Method | Endpoint                                                 | Description               | Authorization      |
| ------ | -------------------------------------------------------- | ------------------------- | ------------------ |
| GET    | `/api/categories`                                        | Retrieve categories       | Authenticated user |
| GET    | `/api/categories/{id}`                                   | Retrieve a category by ID | Authenticated user |
| POST   | `/api/categories?name=Work&description=Work%20tasks`     | Create a category         | ADMIN              |
| PUT    | `/api/categories/1?name=Study&description=Study%20tasks` | Update a category         | ADMIN              |
| DELETE | `/api/categories/1`                                      | Delete a category         | ADMIN              |

Administrative operations require a user with the `ROLE_ADMIN` role.

A newly registered account receives `ROLE_USER` by default. Administrator access must be provisioned separately.

### Authorization test cases

| Scenario                                             | Expected response                                |
| ---------------------------------------------------- | ------------------------------------------------ |
| Regular user retrieves categories                    | `200 OK`                                         |
| Regular user creates a category                      | `403 Forbidden`                                  |
| Regular user updates a category                      | `403 Forbidden`                                  |
| Regular user deletes a category                      | `403 Forbidden`                                  |
| Administrator performs permitted category operations | Successful response, subject to request validity |

For category lookup, an existing category should return `200 OK`; a nonexistent category should return `404 Not Found`.

## Validation and Error Handling

The application validates request data and handles common API errors.

Recommended verification cases:

| Scenario                                                    | Expected behavior  |
| ----------------------------------------------------------- | ------------------ |
| Invalid email or missing required field during registration | `400 Bad Request`  |
| Access a protected task endpoint without a token            | `401 Unauthorized` |
| Access a protected endpoint with an invalid token           | `401 Unauthorized` |
| Register with an existing email                             | Client error       |
| Create a task with a nonexistent category                   | `404 Not Found`    |
| Access another user's task                                  | `403 Forbidden`    |
| Request a negative page number                              | `400 Bad Request`  |
| Request an unsupported page size                            | `400 Bad Request`  |

Exact responses depend on the endpoint's validation and exception-handling configuration. Verify these cases against the running application before treating them as passed tests.

## Database Schema

Flyway migration files are located in:

`src/main/resources/db/migration/`

The initial migration is:

`V1__create_initial_schema.sql`

The schema includes:

| Table        | Purpose                             |
| ------------ | ----------------------------------- |
| `users`      | User account information            |
| `roles`      | Application roles                   |
| `user_roles` | Many-to-many user-role relationship |
| `categories` | Task categories                     |
| `tasks`      | Tasks and their relationships       |

The data model includes:

* User–Role: many-to-many
* User–Task: one-to-many
* Task–Category: many-to-one

The Flyway migration is the schema source for this project's **MySQL** implementation.

**Database note:** This project currently uses MySQL, not PostgreSQL. The migration should not be described as a PostgreSQL dump.

## Project Structure

```text
task-management-api/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/ikaansh/task_management_api/
│   │   └── resources/
│   │       ├── application.properties
│   │       └── db/migration/
│   │           └── V1__create_initial_schema.sql
│   └── test/
├── .mvn/
├── .gitignore
├── mvnw
├── mvnw.cmd
├── pom.xml
└── README.md
```

## Frontend

This submission contains the Spring Boot backend REST API. A separate frontend application is not included. API endpoints can be tested using Postman or consumed by a frontend application.

## Future Improvements

Potential extensions include:

* OpenAPI documentation and Swagger UI.
* Automated integration tests using MockMvc and Testcontainers.
* Category and task-count caching.
* Docker and Docker Compose support.
* Additional task filtering and sorting options.

These are potential extensions, not claims that they are already implemented.

## Author

**Sabirhussen Shaikh**

Java Developer | Spring Boot | REST APIs | SQL

GitHub: https://github.com/Sabirhussen-Shaikh
