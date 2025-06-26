# benspring

> **Local-only Shared Family To-Do List App**  
> A lightweight Spring Boot + PostgreSQL backend, with native iOS & Android clients, push notifications, and presence detection. a full stack project for personal study purposes.
> The project act as a persional notifcation system that triggers based on proximatey so push notafcations are delivered to users when they get home (conect to the local network) to remind them of their tasks.
---

## 📦 Tech Stack

- **Language & Framework**
    - Java 17
    - Spring Boot 3.4.5
    - Spring Data JPA (Hibernate)
    - Spring Security (custom session filter)
    - springdoc-openapi-starter-webmvc-ui (Swagger UI)
    - Hibernate Validator
- **Database**
    - PostgreSQL 42.7.3
- **Build & Dependency Management**
    - Maven
- **Containers & Deployment**
    - Docker (app & DB)
    - Docker Compose
    - Portainer for container management
- **Clients (planned)**
    - iOS: SwiftUI
    - Android: Jetpack Compose
- **Presence Detection**
    - ARP/Nmap on local network
- **Notifications**
    - Local push when user is home and tasks are due

---

## 🗄️ Database Schema

### `user_entity`
| Column          | Type      | Notes                          |
|-----------------|-----------|--------------------------------|
| `id`            | BIGSERIAL | PK                             |
| `username`      | VARCHAR   | unique, not null               |
| `email`         | VARCHAR   | unique, not null               |
| `password_hash` | VARCHAR   | BCrypt hashed password         |
| `role`          | VARCHAR   | `USER` or `ADMIN`              |
| `session_token` | VARCHAR   | nullable; persisted session ID |

### `todo_lists`
| Column        | Type      | Notes                             |
|---------------|-----------|-----------------------------------|
| `id`          | BIGSERIAL | PK                                |
| `title`       | VARCHAR   | not null                          |
| `description` | TEXT      | optional                          |
| `user_id`     | BIGINT    | FK → `user_entity(id)`, not null  |

### `todo_items`
| Column      | Type      | Notes                             |
|-------------|-----------|-----------------------------------|
| `id`        | BIGSERIAL | PK                                |
| `title`     | VARCHAR   | not null                          |
| `completed` | BOOLEAN   | default `false`                   |
| `due_date`  | DATE      | optional                          |
| `list_id`   | BIGINT    | FK → `todo_lists(id)`, not null   |

---

## 🔐 Session Management

1. **On login/register**
    - Generate a secure token (UUID-based).
    - Save it in `user_entity.session_token`.
    - Call `SessionService.createSession(userId, token)` to populate the in-memory map.
2. **On startup**
    - `SessionStartupLoader` fetches all `UserEntity` with non-null sessionToken and calls `createSession(...)`, so sessions persist across restarts.
3. **On each request**
    - `SessionTokenFilter` (a `OncePerRequestFilter`) reads the `Authorization` header, calls `validateSession(token)`, and blocks if not found.
4. **On logout**
    - Clear `UserEntity.session_token`, save user, and `invalidateSession(token)`.

---

## 🚡 REST API Endpoints

### Authentication (`/api/auth`)
| Method | Path        | Body                                | Response                |
|:------:|-------------|-------------------------------------|-------------------------|
| POST   | `/register` | `{ "username", "email", "password" }` | `{ "token", … }`        |
| POST   | `/login`    | `{ "username", "password" }`        | `{ "token", … }`        |
| PUT    | `/logout`   | *(header)* `Authorization: <token>` | `{ "success": true, "message": … }` |
The backend resolves the authenticated user exclusively via the session token now so.
You no longer need to (or should) send the userId in request bodies or query parameters.

### To-Do Lists (`/api/todolists`)
> **All require** `Authorization: <token>` header.  
| Method | Path                         | Body                                              | Response                             |
|:------:|------------------------------|---------------------------------------------------|--------------------------------------|
| GET    | `/api/todolists`             | *none*                                            | `List<ToDoListResponseDTO>`          |
| POST   | `/api/todolists`             | `ToDoListRequestDTO {title, description}` | `ToDoListResponseDTO`                |
| PUT    | `/api/todolists/{listId}`    | `ToDoListRequestDTO {title, description}` | `ToDoListResponseDTO`                |
| DELETE | `/api/todolists/{listId}`    | *none*                                            | `List<ToDoListResponseDTO>`          |

### To-Do Items
> **All require** `Authorization: <token>` header.  
| Method | Path                                    | Body                                                     | Response                         |
|:------:|-----------------------------------------|----------------------------------------------------------|----------------------------------|
| GET    | `/api/todolists/{listId}/todos`         | *none*                                                   | `List<ToDoItemResponseDTO>`      |
| POST   | `/api/todolists/{listId}/todos`         | `ToDoItemRequestDTO {name, completed, dueDate}`          | `ToDoItemResponseDTO`            |
| PUT    | `/api/todos/{itemId}`                   | `ToDoItemRequestDTO {name, completed, dueDate, listId}`  | `ToDoItemResponseDTO`            |
| DELETE | `/api/todos/{itemId}`                   | *none*                                                   | `{ "message": "deleted" }`       |

---

## 📋 Project Checklist & Roadmap

### ✅ Backend: User & Session
- [x] Register users
- [x] Login users
- [x] Password encryption (BCrypt)
- [x] Duplicate username/email detection
- [x] Persist session tokens in DB & in-memory
- [x] Restore sessions after restart
- [x] Logout (invalidate token & clear DB)
- [x] Role system (USER/Admin)
- [x] Admin-only delete user

### ✅ Backend: To-Do Lists
- [x] Create list
- [x] Associate list with user
- [x] Get all lists for user
- [x] Update list by `{listId}`
- [x] Delete list by `{listId}`
- [ ] Enforce owner-only edits/deletes

### ✅ Backend: To-Do Items
- [x] Create item in list
- [x] Get all items in a list
- [x] Update item by `{itemId}`
- [x] Delete item by `{itemId}`
- [ ] Enforce owner-only edits/deletes

### 🛠️ Deployment & DevOps
- [x] Dockerize Spring Boot app
- [x] Docker Compose with PostgreSQL
- [x] Portainer stack
- [ ] Multi-env configs (dev/uat/prod)
- [ ] Migrate sessions to dedicated table

### 📱 Mobile Clients (Upcoming)
- [ ] iOS (SwiftUI)
- [ ] Android (Jetpack Compose)

### 📡 Presence & Notifications
- [ ] ARP/Nmap home detection
- [ ] Local push notifications for due tasks

### ✅ Testing & QA
- [x] Partial unit tests
- [ ] Full suite for controllers
- [ ] E2E tests (Postman/Newman)
- [ ] Web UI test interface

### 📄 Documentation
- [x] Code comments & API docs (Swagger)
- [x] README improvements (this!)
- [ ] API reference & examples

---

## 🚀 Running Locally
This is a very quick explanation I plan to write this in more detail when I can. 
But its simpley: 

* Deploy a postgres database using docker,

* Configure in application.properties to point to the database,

* Build the artifact using maven clean build command,

* Build a docker image with the artifact (using an ubuntu jdk 17 docker image)

* Deploy that docker image.

you can access the API at http://localhost:8080/api

and the swagger docks at http://localhost:8080/swagger-ui/index.html

1. **Copy & configure**
   ```bash
   cp src/main/resources/application.yml.example src/main/resources/application.yml
   # Edit DB URL, credentials, etc.

2. **Build**
   ```bash
   mvn clean package

3. **Start services**
   ```bash
   docker-compose up --build

4. **Access**
   API: http://localhost:8080/api

   Swagger UI: http://localhost:8080/swagger-ui/index.html

## 🔗 Further Resources
* Swagger UI: /swagger-ui/index.html

* API docs (OpenAPI): /v3/api-docs

* PostgreSQL JDBC: see application.yml

* Maven dependencies: see pom.xml