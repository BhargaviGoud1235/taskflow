# TaskFlow — Project & Task Management System

A full-stack task management application: a **Spring Boot + Spring Security (JWT) + MySQL** backend paired with a **vanilla JavaScript** frontend dashboard. Users register, log in, create projects, add tasks, and move tasks across a To Do / In Progress / Done board — all backed by a secured REST API.

Built as a portfolio project to demonstrate authentication, authorization, and full-stack integration — skills that go beyond a basic CRUD API.

## Features

- **JWT-based authentication** — register and log in, receive a signed token, use it to access protected endpoints
- **Password hashing** with BCrypt (passwords are never stored in plain text)
- **Authorization rules** — only a project's owner can update or delete it
- **Project & task management** — create projects, add tasks, assign tasks to users, track priority and due dates
- **Task board** — move tasks between To Do → In Progress → Done from the frontend
- **Centralized exception handling** with correct HTTP status codes (401, 403, 404, 409)
- **Unit tests** for the authentication service using JUnit 5 and Mockito
- **CORS configured** so the frontend can call the API from a different origin

## Tech Stack

| Layer          | Technology                              |
|----------------|-------------------------------------------|
| Language       | Java 17                                    |
| Framework      | Spring Boot 3.3.2, Spring Security         |
| Auth           | JWT (jjwt library), BCrypt password hashing|
| Data Access    | Spring Data JPA (Hibernate)                |
| Database       | MySQL                                      |
| Frontend       | HTML, CSS, vanilla JavaScript (fetch API)  |
| Build Tool     | Maven                                      |
| Testing        | JUnit 5, Mockito, H2 (in-memory)           |

## Architecture

```
Frontend (HTML/CSS/JS) → REST API → Controller → Service → Repository → MySQL
                                        ↑
                                  JWT Auth Filter
```

```
src/main/java/com/bhargavi/taskflow/
├── controller/     REST endpoints (Auth, Project, Task)
├── service/        Business logic interfaces + impl/
├── repository/     Spring Data JPA repositories
├── model/          JPA entities (User, Project, Task) + enums
├── dto/            Request/response objects
├── security/       JWT service, filter, UserDetailsService
├── config/         Spring Security configuration
└── exception/      Custom exceptions + global handler

frontend/
├── index.html      Login/register screen + dashboard
├── css/style.css   Styling
└── js/app.js       API calls, state management, rendering
```

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.6+
- MySQL 8.x running locally

### 1. Clone and configure

```bash
git clone https://github.com/BhargaviGoud1235/taskflow.git
cd taskflow
```

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.username=root
spring.datasource.password=your_mysql_password
jwt.secret=ReplaceThisWithYourOwnLongRandomSecretKeyAtLeast256Bits
```

**Important:** change `jwt.secret` to your own random string before using this anywhere beyond your local machine — never commit real secrets to GitHub.

### 2. Run the backend

```bash
mvn spring-boot:run
```

The API starts on `http://localhost:8080`.

### 3. Run the frontend

The frontend is plain HTML/CSS/JS with no build step. Just open `frontend/index.html` directly in your browser, or serve it with a simple local server:

```bash
cd frontend
python3 -m http.server 5500
```

Then visit `http://localhost:5500`.

### 4. Run tests

```bash
mvn test
```

## API Endpoints

### Auth (public)

| Method | Endpoint             | Description         |
|--------|-----------------------|----------------------|
| POST   | `/api/auth/register`  | Create an account    |
| POST   | `/api/auth/login`     | Log in, get a JWT    |

### Projects (requires `Authorization: Bearer <token>`)

| Method | Endpoint              | Description                    |
|--------|------------------------|---------------------------------|
| POST   | `/api/projects`        | Create a project                |
| GET    | `/api/projects/mine`   | List projects you own           |
| GET    | `/api/projects/{id}`   | Get a project by ID             |
| PUT    | `/api/projects/{id}`   | Update a project (owner only)   |
| DELETE | `/api/projects/{id}`   | Delete a project (owner only)   |

### Tasks (requires `Authorization: Bearer <token>`)

| Method | Endpoint                          | Description                |
|--------|------------------------------------|------------------------------|
| POST   | `/api/tasks`                      | Create a task                |
| GET    | `/api/tasks/project/{projectId}`  | List tasks in a project      |
| GET    | `/api/tasks/assigned/{userId}`    | List tasks assigned to a user|
| PATCH  | `/api/tasks/{id}/status`          | Update task status           |
| PATCH  | `/api/tasks/{id}/assign/{userId}` | Assign a task to a user      |
| DELETE | `/api/tasks/{id}`                 | Delete a task                |

### Sample — register

```json
POST /api/auth/register
{
  "name": "Bhargavi",
  "email": "bhargavi@example.com",
  "password": "securepass123"
}
```

Response includes a `token` — send it as `Authorization: Bearer <token>` on every subsequent request.

## Possible Next Steps

- Add role-based access (ADMIN can manage all projects, USER only their own)
- Add refresh tokens instead of a single long-lived JWT
- Add pagination to task/project lists
- Add Swagger/OpenAPI docs
- Deploy backend (Render/Railway) and frontend (Vercel/Netlify) for a live demo link

## Author

**Pandula Bhargavi**
B.Tech CSE (AI & ML), VVIT, Guntur
