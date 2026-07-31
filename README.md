# ProjectVault — Backend

ProjectVault is a full-stack portfolio platform designed for 3D artists. It lets artists publish projects, document their creative process, and present more than the final render.

This repository contains the Spring Boot REST API. It manages authentication, users, projects, media, portfolio data, and administrative operations for the ProjectVault frontend.

## Live application

- [ProjectVault](https://projectvault-seven.vercel.app)
- Backend base URL: `https://projectvault-backend-00k9.onrender.com`

> The backend is hosted on a free-tier service, so the first request may take a short time while the server starts.

## Core capabilities

- Registration and login with stateless JWT authentication
- Password hashing with BCrypt
- Role-based authorization for users and administrators
- Public, searchable, and paginated project gallery
- Personal profile and portfolio management
- Project creation, editing, visibility controls, and deletion
- Project categorization and software tagging
- Image and avatar uploads through Cloudinary
- Separate main, gallery, and timelapse image types with custom ordering
- Featured-project management and administrative statistics
- Request validation and centralized API error handling
- Configurable welcome email after registration
- Configurable CORS for the frontend application

## Tech stack

- Java 25
- Spring Boot 4
- Spring Web MVC
- Spring Security
- Spring Data JPA and Hibernate
- PostgreSQL
- JSON Web Tokens with JJWT
- Jakarta Bean Validation
- Cloudinary
- Spring Mail
- Maven
- Docker
- Render

## Application structure

The project follows a layered architecture:

- `controllers` — REST endpoints and request validation
- `services` — application and business logic
- `repositories` — database access through Spring Data JPA
- `entities` — relational domain model
- `payloads` — request and response DTOs
- `security` — JWT generation, validation, filtering, and access rules
- `exceptions` — centralized error responses
- `config` — external service configuration

## API overview

| Area | Main endpoints | Access |
| --- | --- | --- |
| Authentication | `POST /auth/register`, `POST /auth/login` | Public |
| Gallery | `GET /projects`, `GET /projects/featured`, `GET /projects/{id}` | Public |
| Project media | `GET /projects/{id}/images` | Public |
| Categories and software | `GET /categories`, `GET /softwares` | Public |
| Current user | `GET /users/me`, `PUT /users/me`, `POST /users/me/avatar` | Authenticated |
| Personal portfolio | `GET /users/me/projects`, `POST /projects`, `PUT /projects/{id}`, `DELETE /projects/{id}` | Authenticated |
| Media management | Upload, update, order, and delete project images | Authenticated |
| Administration | Users, projects, featured content, statistics, categories, and software | Admin |

Public project results support pagination as well as optional title and category filters.

## Getting started

### Prerequisites

- Java 25
- PostgreSQL
- A Cloudinary account for media uploads
- Optional SMTP credentials for welcome emails

The Maven Wrapper is included, so a separate Maven installation is not required.

### Installation

1. Clone the repository:

   ```bash
   git clone https://github.com/Solis3D/projectvault-backend.git
   cd projectvault-backend
   ```

2. Create a PostgreSQL database named `projectvault`, or configure a different database URL.

3. Copy `env.properties.example` to `env.properties` and replace the example values:

   ```powershell
   Copy-Item env.properties.example env.properties
   ```

   `env.properties` is ignored by Git and must never contain credentials that are committed to the repository.

4. Start the application on Windows:

   ```powershell
   .\mvnw.cmd spring-boot:run
   ```

   On macOS or Linux:

   ```bash
   ./mvnw spring-boot:run
   ```

The API runs at `http://localhost:8080` by default. On startup, the application seeds its initial project categories, 3D software list, and administrator account from the configured environment values.

## Configuration

The main configuration groups are:

- PostgreSQL connection: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`
- JWT: `JWT_SECRET`, `JWT_EXPIRATION`
- Cloudinary: `CLOUDINARY_CLOUD_NAME`, `CLOUDINARY_API_KEY`, `CLOUDINARY_API_SECRET`
- Administrator seed: `ADMIN_FIRST_NAME`, `ADMIN_LAST_NAME`, `ADMIN_USERNAME`, `ADMIN_EMAIL`, `ADMIN_PASSWORD`
- Frontend and CORS: `FRONTEND_URL`, `CORS_ALLOWED_ORIGINS`
- Optional email service: `MAIL_ENABLED` and the `MAIL_*` variables

For JWT signing, use a long, randomly generated secret of at least 32 bytes. Multiple allowed CORS origins can be separated with commas.

## Useful commands

```powershell
# Run the tests
.\mvnw.cmd test

# Create the application package
.\mvnw.cmd clean package

# Build the Docker image
docker build -t projectvault-backend .
```

## Related repository

[ProjectVault Frontend](https://github.com/Solis3D/projectvault-frontend)

## Project context

ProjectVault was developed as the final capstone project for the Full Stack Developer program at EPICODE. The concept combines my web development training with my background in 3D Environment Art, creating a platform around the way digital artists organize and present their work.

## Author

Davide Aversano — [GitHub](https://github.com/Solis3D)
