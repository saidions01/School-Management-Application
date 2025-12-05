# School Management System - Full Stack Application

A complete School Management Application built with Spring Boot (Backend), Angular (Frontend), MySQL (Database), and Docker (Containerization).

## Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Installation & Setup](#installation--setup)
- [Running with Docker](#running-with-docker)
- [API Documentation](#api-documentation)
- [Project Structure](#project-structure)
- [Testing](#testing)
- [Development](#development)

## Features

### Backend (Spring Boot)

- **Authentication System**
  - JWT-based authentication
  - Admin login and registration
  - Rate limiting (5 attempts per minute)
  - Password hashing with BCrypt

- **Student Management**
  - Full CRUD operations
  - Search by username
  - Filter by level
  - Pagination support
  - CSV import/export functionality

- **Security**
  - Global exception handling
  - Clean JSON error responses
  - Input validation with DTOs
  - Protected endpoints requiring JWT token

- **Documentation**
  - Swagger/OpenAPI integration
  - Proper HTTP status codes (200, 201, 400, 401, 404, 409, 500)
  - Comprehensive API documentation

### Frontend (Angular)

- **Login Page**
  - Admin authentication
  - JWT token storage
  - Error handling

- **Students Management Page**
  - View all students with pagination
  - Create new student
  - Edit existing student
  - Delete student
  - Search students by username
  - Filter students by level
  - Export students to CSV
  - Import students from CSV

- **UI/UX**
  - Responsive design
  - Clean and intuitive interface
  - Loading states
  - Success/error messages
  - Modal forms for CRUD operations

## Tech Stack

### Backend
- **Framework**: Spring Boot 3.4.12
- **Java Version**: Java 21
- **Database**: MySQL 8.0
- **Authentication**: JWT (io.jsonwebtoken)
- **API Documentation**: Springdoc-OpenAPI 2.0.2
- **ORM**: JPA/Hibernate
- **Build Tool**: Gradle
- **Testing**: JUnit 5, Mockito

### Frontend
- **Framework**: Angular 21
- **Language**: TypeScript
- **HTTP Client**: HttpClient with Interceptors
- **CSS**: Custom styling with responsive design
- **Testing**: Jasmine/Karma

### Database
- **MySQL**: 8.0 with persistent storage

### Containerization
- **Docker**: Multi-stage builds for optimized images
- **Docker Compose**: Orchestration of services

## Prerequisites

### For Local Development
- Java 21 or higher
- Node.js 21 or higher
- npm 10.9.2 or higher
- Maven 3.8.1 or higher
- MySQL 8.0 (optional, for local development)
- Git

### For Docker
- Docker 20.10 or higher
- Docker Compose 1.29 or higher

## Installation & Setup

### 1. Clone the Repository

```bash
git clone <repository-url>
cd school-management-system
```

### 2. Backend Setup (Local Development)

```bash
cd backend/SMA

# Configure database in src/main/resources/application.properties
# Update spring.datasource.url, username, and password if needed

# Build the project
./gradlew build

# Run the application
./gradlew bootRun
```

The backend will be available at `http://localhost:8080/api`
Swagger UI: `http://localhost:8080/api/swagger-ui.html`

### 3. Frontend Setup (Local Development)

```bash
cd frontend/school-management-ui

# Install dependencies
npm install

# Run the development server
npm start
```

The frontend will be available at `http://localhost:4200`

## Running with Docker

### Prerequisites
- Docker and Docker Compose installed

### Setup and Run

```bash
# From the project root directory
docker-compose up --build
```

This will:
1. Build the MySQL database container
2. Build the Spring Boot backend container
3. Build the Angular frontend container
4. Start all services and link them together

**Access the application:**
- Frontend: http://localhost:4200
- Backend API: http://localhost:8080/api
- Swagger UI: http://localhost:8080/api/swagger-ui.html

### Stopping the Application

```bash
docker-compose down
```

To remove volumes as well (clean database):
```bash
docker-compose down -v
```

## API Documentation

### Authentication Endpoints

#### Login
```
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "password123"
}

Response (200):
{
  "statusCode": 200,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGc...",
    "type": "Bearer",
    "username": "admin",
    "id": 1
  }
}
```

#### Register
```
POST /api/auth/register
Content-Type: application/json

{
  "username": "newadmin",
  "password": "password123",
  "confirmPassword": "password123"
}

Response (201):
{
  "statusCode": 201,
  "message": "Admin registered successfully",
  "data": {
    "token": "eyJhbGc...",
    "type": "Bearer",
    "username": "newadmin",
    "id": 2
  }
}
```

### Student Endpoints (All require JWT token in Authorization header)

#### Get All Students (with Pagination)
```
GET /api/students?page=0&size=10
Authorization: Bearer <token>

Response (200):
{
  "statusCode": 200,
  "message": "Students retrieved successfully",
  "data": {
    "content": [...],
    "totalElements": 50,
    "totalPages": 5,
    "size": 10,
    "number": 0
  }
}
```

#### Get Student by ID
```
GET /api/students/{id}
Authorization: Bearer <token>

Response (200):
{
  "statusCode": 200,
  "message": "Student retrieved successfully",
  "data": {
    "id": 1,
    "username": "john_doe",
    "level": "FIRST_YEAR"
  }
}
```

#### Create Student
```
POST /api/students
Authorization: Bearer <token>
Content-Type: application/json

{
  "username": "new_student",
  "level": "FIRST_YEAR"
}

Response (201):
{
  "statusCode": 201,
  "message": "Student created successfully",
  "data": {
    "id": 1,
    "username": "new_student",
    "level": "FIRST_YEAR"
  }
}
```

#### Update Student
```
PUT /api/students/{id}
Authorization: Bearer <token>
Content-Type: application/json

{
  "username": "updated_username",
  "level": "SECOND_YEAR"
}

Response (200):
{
  "statusCode": 200,
  "message": "Student updated successfully",
  "data": {
    "id": 1,
    "username": "updated_username",
    "level": "SECOND_YEAR"
  }
}
```

#### Delete Student
```
DELETE /api/students/{id}
Authorization: Bearer <token>

Response (200):
{
  "statusCode": 200,
  "message": "Student deleted successfully"
}
```

#### Search Students
```
GET /api/students/search?username=john&page=0&size=10
Authorization: Bearer <token>

Response (200): Returns paginated search results
```

#### Filter by Level
```
GET /api/students/filter/level?level=FIRST_YEAR&page=0&size=10
Authorization: Bearer <token>

Response (200): Returns paginated results filtered by level
```

#### Export to CSV
```
GET /api/students/export
Authorization: Bearer <token>

Response (200): CSV file download
```

#### Import from CSV
```
POST /api/students/import
Authorization: Bearer <token>
Content-Type: multipart/form-data

Form Data: file=students.csv

Response (200):
{
  "statusCode": 200,
  "message": "CSV imported successfully",
  "data": [
    {
      "username": "student1",
      "level": "FIRST_YEAR"
    }
  ]
}
```

## HTTP Status Codes

| Code | Description | Usage |
|------|-------------|-------|
| 200 | OK | Successful GET, PUT, DELETE request |
| 201 | Created | Successful POST request creating a resource |
| 400 | Bad Request | Invalid request data or missing required fields |
| 401 | Unauthorized | Missing or invalid JWT token |
| 404 | Not Found | Requested resource does not exist |
| 409 | Conflict | Resource already exists (duplicate username) |
| 500 | Internal Server Error | Unexpected server error |

## Project Structure

```
school-management-system/
├── backend/
│   ├── SMA/
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── java/test/technique/SMA/
│   │   │   │   │   ├── SmaApplication.java
│   │   │   │   │   ├── controller/
│   │   │   │   │   ├── service/
│   │   │   │   │   ├── entity/
│   │   │   │   │   ├── repository/
│   │   │   │   │   ├── dto/
│   │   │   │   │   ├── security/
│   │   │   │   │   ├── exception/
│   │   │   │   │   └── config/
│   │   │   │   └── resources/
│   │   │   │       └── application.properties
│   │   │   └── test/
│   │   │       └── java/test/technique/SMA/
│   │   │           └── service/
│   │   ├── build.gradle
│   │   └── settings.gradle
│   └── Dockerfile
├── frontend/
│   ├── school-management-ui/
│   │   ├── src/
│   │   │   ├── app/
│   │   │   │   ├── app.routes.ts
│   │   │   │   ├── app.config.ts
│   │   │   │   ├── core/
│   │   │   │   │   ├── services/
│   │   │   │   │   ├── guards/
│   │   │   │   │   └── interceptors/
│   │   │   │   └── features/
│   │   │   │       ├── auth/
│   │   │   │       └── students/
│   │   │   └── main.ts
│   │   ├── package.json
│   │   └── angular.json
│   └── Dockerfile
├── docker-compose.yml
└── README.md
```

## Testing

### Backend Unit Tests

Run all tests:
```bash
cd backend/SMA
./gradlew test
```

Run specific test:
```bash
./gradlew test --tests AuthenticationServiceTest
./gradlew test --tests StudentServiceTest
```

Test classes:
- `AuthenticationServiceTest`: Tests for authentication service (login, register, rate limiting)
- `StudentServiceTest`: Tests for student CRUD operations

### Frontend Unit Tests (Optional)

```bash
cd frontend/school-management-ui
npm test
```

## Development

### First Time Setup

1. **Create an admin account** (only needed once):
   ```bash
   POST http://localhost:8080/api/auth/register
   {
     "username": "admin",
     "password": "admin123",
     "confirmPassword": "admin123"
   }
   ```

2. **Login with created credentials**:
   - Go to http://localhost:4200/login
   - Enter admin credentials
   - You'll be redirected to the students page

3. **Create test students**:
   - Use the "Add New Student" button
   - Fill in the form and click Create

### Making Changes

**Backend Changes:**
1. Make code changes
2. Rebuild: `./gradlew build`
3. Restart: `./gradlew bootRun`

**Frontend Changes:**
1. Make code changes
2. Browser will auto-reload due to ng serve watch mode

**With Docker:**
1. Make changes
2. Rebuild containers: `docker-compose up --build`

## Security Considerations

1. **JWT Expiration**: Set in `application.properties` (default: 24 hours)
2. **Rate Limiting**: 5 login attempts per minute per username
3. **Password Hashing**: BCrypt with default strength
4. **CORS**: Configured for localhost:4200 (update for production)
5. **Validation**: All inputs validated at service layer

## Known Limitations

1. Frontend currently serves from development build
2. No database backup strategy implemented
3. Single admin management (multi-admin requires architectural changes)
4. No email notification system
5. No audit logging system

## Future Improvements

1. Role-based access control (RBAC)
2. Email notifications
3. Advanced search with filters
4. Student grade management
5. Attendance tracking
6. Mobile app support
7. Real-time notifications
8. Analytics dashboard
9. Batch operations
10. User activity audit logs

## Troubleshooting

### Docker Issues

**Container fails to start:**
```bash
# Check logs
docker-compose logs backend
docker-compose logs frontend
docker-compose logs mysql
```

**Port already in use:**
```bash
# Change ports in docker-compose.yml
ports:
  - "8081:8080"  # Use 8081 instead of 8080
```

### Database Issues

**Cannot connect to MySQL:**
- Check if MySQL container is running: `docker ps`
- Verify credentials in `application.properties`
- Wait for MySQL to be ready (health check)

**Database tables not created:**
- Check `spring.jpa.hibernate.ddl-auto=update` setting
- Check application logs for SQL errors

### Frontend Issues

**CORS errors:**
- Backend CORS configuration in `SecurityConfig`
- Frontend API URL must match backend server

**Token not sent with requests:**
- Check auth interceptor is registered
- Verify token is stored in localStorage
- Check Authorization header in network tab

## Contributing

1. Create a feature branch
2. Make changes with clear commit messages
3. Test thoroughly
4. Submit pull request

## License

This project is for educational purposes.

## Support

For issues or questions, please open an issue on the repository.

---

**Last Updated**: December 5, 2025
**Version**: 1.0.0
"# School-Management-Application" 
