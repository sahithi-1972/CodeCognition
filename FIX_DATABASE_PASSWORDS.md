# 🔐 CodeCognition - Fix Database User Passwords

## Problem
Frontend login is failing with "Invalid email or password" because the password hashes in the database are incorrect.

## Solution
You need to update the database with **correct BCrypt password hashes**.

---

## Step 1: Open MySQL Command Line

```bash
mysql -u root -p
```

When prompted, enter your MySQL root password.

---

## Step 2: Run These Commands (One at a Time)

### First, select the database:
```sql
USE codecognition_db;
```

### Then, delete old users:
```sql
DELETE FROM users;
```

### Insert ADMIN user with correct hash:
```sql
INSERT INTO users (email, password, full_name, role, is_active, created_at) VALUES
('admin@codecognition.ai', '$2a$10$h1L2c0duFmxGLc9DvPi5KeVT/YWbUx7NYXcZvzC16k2zLkM7eZi.O', 'Admin User', 'ADMIN', TRUE, NOW());
```

### Insert REGULAR user with correct hash:
```sql
INSERT INTO users (email, password, full_name, role, is_active, created_at) VALUES
('user@codecognition.ai', '$2a$10$9dGR1Lky/FqXLkTNp2CXOukU0aFNCuMVSFjTLR1VN9MlNvC2IrS8W', 'Demo Customer', 'USER', TRUE, NOW());
```

### Verify the users were inserted:
```sql
SELECT id, email, full_name, role, is_active FROM users;
```

**You should see 2 rows with the users above.**

---

## Step 3: Test Login in Frontend

Once users are in database, try logging in with:

### ADMIN
- Email: `admin@codecognition.ai`
- Password: `Admin@123`

### OR CUSTOMER
- Email: `user@codecognition.ai`
- Password: `User@123`

---

## Credentials Reference

| Role | Email | Password | Hash |
|------|-------|----------|------|
| **Admin** | admin@codecognition.ai | Admin@123 | $2a$10$h1L2c0duFmxGLc9DvPi5KeVT/YWbUx7NYXcZvzC16k2zLkM7eZi.O |
| **User** | user@codecognition.ai | User@123 | $2a$10$9dGR1Lky/FqXLkTNp2CXOukU0aFNCuMVSFjTLR1VN9MlNvC2IrS8W |

---

## 🚀 Full Example (Copy-Paste All at Once)

If you want to paste everything at once in MySQL, use this script file:

**File location:** `c:\Users\sahit\CodeCognition\backend-java\INSERT_CORRECT_USERS.sql`

You can load it with:
```bash
mysql -u root -p < "C:\Users\sahit\CodeCognition\backend-java\INSERT_CORRECT_USERS.sql"
```

Or manually in MySQL:
```sql
source C:\Users\sahit\CodeCognition\backend-java\INSERT_CORRECT_USERS.sql;
```

---

## If It Still Doesn't Work

1. **Verify users exist:**
   ```sql
   SELECT * FROM users;
   ```

2. **Test backend directly:** Go to `http://localhost:8000/swagger-ui.html` and try the login endpoint

3. **Check browser console:** Press F12 → Console tab → look for network errors

4. **Restart services:**
   - Kill Java backend: `Ctrl+C`
   - Restart: `java -jar target/codecognition-ai-3.3.0.jar`
   - Kill Node frontend: `Ctrl+C`
   - Restart: `npm run dev`

---

**Once done, password validation will work correctly!** ✅
