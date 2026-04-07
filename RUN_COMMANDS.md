# CodeCognition - Run & Setup Guide

## 1. Insert Demo Users into Database

Run this in MySQL directly (open MySQL CLI and execute):

```sql
USE codecognition_db;

-- Admin User (can manage all)
-- Email: admin@codecognition.ai
-- Password: Admin@123
INSERT INTO users (email, password, full_name, role, is_active) 
VALUES ('admin@codecognition.ai', '$2a$10$dXj3SW6G7P50eS6DQluzv.JJsDHzmIQVeUvMQe8s5UjGsZ516dm7m', 'Admin User', 'ADMIN', TRUE);

-- Customer User (regular user)
-- Email: user@codecognition.ai
-- Password: User@123
INSERT INTO users (email, password, full_name, role, is_active) 
VALUES ('user@codecognition.ai', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36gBUSPm', 'Demo Customer', 'USER', TRUE);

-- Verify users were inserted
SELECT id, email, full_name, role, is_active FROM users;
```

## 2. Start Backend (Terminal 1)

```powershell
cd c:\Users\sahit\CodeCognition\backend-java
java -jar target/codecognition-ai-3.3.0.jar
```

**Expected Output:**
```
Tomcat started on port 8000 (http)
Started CodeCognitionApplication in XX.XXX seconds
```

## 3. Start Frontend (Terminal 2)

```powershell
cd c:\Users\sahit\CodeCognition\frontend
npm install
npm run dev
```

**Expected Output:**
```
VITE v5.2.0 ready in XX ms
➜  Local: http://localhost:5173/
```

## 4. Test in Swagger UI

Once both are running, go to: **http://localhost:8000/swagger-ui.html**

### Step 1: Login as Admin
1. Expand `POST /auth/login`
2. Click **Try it out**
3. Enter:
```json
{
  "email": "admin@codecognition.ai",
  "password": "Admin@123"
}
```
4. Click **Execute**
5. Copy the `token` from response

### Step 2: Authorize Swagger UI
1. Click the lock icon (top right) → **Authorize**
2. Paste: `Bearer {YOUR_TOKEN}` (replace {YOUR_TOKEN} with copied token)
3. Click **Authorize** → **Close**

### Step 3: Test Protected Endpoints
- `GET /ping` - Should return 200 ✅
- `POST /analyze-repo` - Analyze a repository
- `POST /analyze` - Queue analysis

## 5. Test Sign Up (Create New User)

1. Expand `POST /auth/register`
2. Click **Try it out**
3. Enter:
```json
{
  "email": "newuser@example.com",
  "password": "SecurePass@123",
  "fullName": "New User"
}
```
4. Click **Execute**
5. You'll get a token - use this to login

## Demo Credentials

| Role | Email | Password |
|------|-------|----------|
| Admin | admin@codecognition.ai | Admin@123 |
| Customer | user@codecognition.ai | User@123 |

---

## What Changed in Backend

✅ Fixed Swagger UI rendering issue
✅ Added JWT security requirement to `/auth/verify` endpoint
✅ Added demo users to database with BCrypt hashed passwords
✅ Both endpoints public and protected are now working

## Frontend Integration Ready

The backend is now ready for frontend authentication integration:
- Sign Up: `POST /auth/register`
- Login: `POST /auth/login`
- Protected endpoints require: `Authorization: Bearer {JWT_TOKEN}`
