# CodeCognition Backend Testing Guide

## Quick Start

### 1. Start the Backend
```bash
cd backend-java
mvn spring-boot:run
```
Server runs on: `http://localhost:8000`

## API Endpoints

### 🔓 Public Endpoints (No Auth Required)

#### Health Check
```bash
GET /ping
```
Response:
```json
{
  "status": "online",
  "service": "CodeCognition AI v3.3 (Java)",
  "ts": "2026-04-07T00:15:30"
}
```

---

### 🔐 Authentication Endpoints

#### Register User
```bash
POST /auth/register
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "securePassword123",
  "fullName": "John Doe"
}
```

Response (201 Created):
```json
{
  "id": 1,
  "email": "user@example.com",
  "fullName": "John Doe",
  "role": "USER",
  "createdAt": "2026-04-07T00:15:30"
}
```

---

#### Login User
```bash
POST /auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "securePassword123"
}
```

Response (200 OK):
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "userId": "1",
  "email": "user@example.com",
  "role": "USER",
  "expiresIn": 86400000
}
```

---

### 🔐 Protected Endpoints (Requires JWT Token)

#### Get Health Status
```bash
GET /health-status?repo_url=owner/repo
Authorization: Bearer <JWT_TOKEN>
```

#### Analyze Repository
```bash
POST /analyze-repo
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json

{
  "owner": "torvalds",
  "repo": "linux",
  "language": "C",
  "stars": 150000,
  "description": "Linux kernel"
}
```

#### Run Simulation
```bash
POST /simulation
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json

{
  "changed_file": "src/main/java/App.java",
  "repo_url": "owner/repo"
}
```

#### Get Quantum Risk
```bash
GET /quantum-risk?repo_url=owner/repo
Authorization: Bearer <JWT_TOKEN>
```

#### Get Agent Logs
```bash
GET /agent-logs
Authorization: Bearer <JWT_TOKEN>
```

---

## Testing with Postman/cURL

### 1. Register
```bash
curl -X POST http://localhost:8000/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"Test123!","fullName":"Test User"}'
```

### 2. Login (Get Token)
```bash
curl -X POST http://localhost:8000/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"Test123!"}'
```

### 3. Use Token on Protected Endpoint
```bash
curl -X GET http://localhost:8000/health-status \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"
```

---

## Error Responses

### Validation Error (400)
```json
{
  "status": "error",
  "code": "VALIDATION_FAILED",
  "message": "Request validation failed",
  "errors": {
    "email": "must not be blank"
  },
  "timestamp": "2026-04-07T00:15:30"
}
```

### Unauthorized (401)
```json
{
  "status": "error",
  "code": "INVALID_CREDENTIALS",
  "message": "Invalid email or password",
  "timestamp": "2026-04-07T00:15:30"
}
```

### User Already Exists (409)
```json
{
  "status": "error",
  "code": "USER_EXISTS",
  "message": "Email already registered",
  "timestamp": "2026-04-07T00:15:30"
}
```

---

## Security Features Enabled

✅ JWT Token Authentication
✅ Password Hashing (BCrypt)
✅ CORS restricted to localhost:5173
✅ Input Validation
✅ Global Exception Handling
✅ Role-based Authorization (USER/ADMIN)
✅ Stateless Sessions (no cookies)
