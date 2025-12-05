# Quick Start Guide

## Option 1: Run with Docker (Recommended - Easiest)

### Prerequisites
- Docker Desktop (includes Docker and Docker Compose)

### Steps

1. **Navigate to project root**
   ```bash
   cd school-management-system
   ```

2. **Start all services**
   ```bash
   docker-compose up --build
   ```

   Wait for all services to be ready (look for "Application started successfully" in backend logs)

3. **Access the application**
   - Frontend: http://localhost:4200
   - Backend API: http://localhost:8080/api
   - Swagger UI: http://localhost:8080/api/swagger-ui.html

4. **Create your first admin account**
   - The system is pre-configured with database initialization
   - Use the login page to create an account or login

5. **Stop services**
   ```bash
   docker-compose down
   ```

---

## Option 2: Local Development Setup

### Prerequisites
- Java 21+ ([Download](https://adoptium.net/))
- Node.js 21+ ([Download](https://nodejs.org/))
- MySQL 8.0 ([Download](https://dev.mysql.com/downloads/mysql/))

### Backend Setup

1. **Open terminal and navigate to backend**
   ```bash
   cd backend/SMA
   ```

2. **Configure database**
   - Start MySQL server
   - Edit `src/main/resources/application.properties`:
     ```properties
     spring.datasource.url=jdbc:mysql://localhost:3306/school_management
     spring.datasource.username=root
     spring.datasource.password=YOUR_PASSWORD
     ```
   - Create database:
     ```sql
     CREATE DATABASE school_management;
     ```

3. **Build and run**
   ```bash
   # Windows
   gradlew.bat bootRun

   # Mac/Linux
   ./gradlew bootRun
   ```

   Backend will start at http://localhost:8080/api

### Frontend Setup

1. **Open new terminal and navigate to frontend**
   ```bash
   cd frontend/school-management-ui
   ```

2. **Install dependencies**
   ```bash
   npm install
   ```

3. **Start development server**
   ```bash
   npm start
   ```

   Frontend will open at http://localhost:4200

### Testing the Application

1. **Create Admin Account (First Time)**
   ```bash
   # Use Postman or curl:
   POST http://localhost:8080/api/auth/register
   Content-Type: application/json

   {
     "username": "admin",
     "password": "admin123",
     "confirmPassword": "admin123"
   }
   ```

2. **Login**
   - Go to http://localhost:4200/login
   - Enter your admin credentials
   - Click Login

3. **Manage Students**
   - Click "Add New Student"
   - Fill in username and level
   - Click Create
   - Use search, filter, export, import features as needed

---

## Troubleshooting

### Docker Issues

**Port already in use:**
Edit `docker-compose.yml` and change ports:
```yaml
services:
  backend:
    ports:
      - "8081:8080"  # Use 8081 instead of 8080
  frontend:
    ports:
      - "4201:4200"  # Use 4201 instead of 4200
```

**Container won't start:**
```bash
# Check logs
docker-compose logs backend
docker-compose logs frontend
docker-compose logs mysql

# Rebuild from scratch
docker-compose down -v
docker-compose up --build
```

### Local Development Issues

**Backend won't start - Database connection error:**
- Ensure MySQL is running
- Check username/password in `application.properties`
- Create database: `CREATE DATABASE school_management;`

**Frontend won't start - Port already in use:**
```bash
# On Windows
netstat -ano | findstr :4200
taskkill /PID <PID> /F

# On Mac/Linux
lsof -i :4200
kill -9 <PID>
```

**Gradle build fails:**
```bash
# Clean and rebuild
gradlew clean build -x test
```

---

## API Testing

Use any of these tools:
- **Postman**: [Download](https://www.postman.com/downloads/)
- **Insomnia**: [Download](https://insomnia.rest/)
- **VS Code REST Client**: Install extension "REST Client"
- **curl**: Use from terminal

### Example: Get All Students
```bash
curl -X GET "http://localhost:8080/api/students" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

---

## Development Tips

### Backend
- API runs on port 8080
- Swagger docs: http://localhost:8080/api/swagger-ui.html
- Database: MySQL on port 3306
- Changes require rebuild: `gradlew bootRun`

### Frontend
- Dev server runs on port 4200
- Changes auto-reload during development
- Network tab (F12) shows all API calls
- Check localStorage for JWT token

### Database
- Access MySQL directly:
  ```bash
  mysql -u root -p school_management
  ```
- View tables:
  ```sql
  SHOW TABLES;
  DESC students;
  DESC admins;
  ```

---

## Next Steps After Setup

1. ✅ Create admin account
2. ✅ Login to application
3. ✅ Add some test students
4. ✅ Test search and filter features
5. ✅ Export to CSV
6. ✅ Import from CSV
7. ✅ Test CRUD operations
8. ✅ Review API docs in Swagger UI
9. ✅ Run unit tests: `gradlew test`
10. ✅ Deploy to production (update URLs)

---

## Documentation

For detailed documentation, see:
- [Full README.md](../README.md) - Complete project documentation
- [API Documentation](../README.md#api-documentation) - API endpoints reference
- [Project Structure](../README.md#project-structure) - File organization
- [Troubleshooting](../README.md#troubleshooting) - Common issues

---

Need help? Check the README.md or review error messages carefully!
