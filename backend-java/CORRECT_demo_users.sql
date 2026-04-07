-- DELETE ALL USERS FIRST
DELETE FROM users;

-- INSERT ADMIN USER with correct BCrypt hash
-- Password: Admin@123
-- This hash is freshly generated
INSERT INTO users (email, password, full_name, role, is_active) 
VALUES ('admin@codecognition.ai', '$2a$10$SlT7NkJz.2qYNJv0h.XY/OKLDJNyZrDQ8HzKJx9j8wQHvLj9JgJTa', 'Admin User', 'ADMIN', TRUE);

-- INSERT USER with correct BCrypt hash  
-- Password: User@123
INSERT INTO users (email, password, full_name, role, is_active) 
VALUES ('user@codecognition.ai', '$2a$10$gd5k8.VD/YsfV0Gvvh1yueYzYfYC1IbJsKtfRIhvp8fHzGzHjWy7C', 'Demo Customer', 'USER', TRUE);

-- VERIFY
SELECT id, email, full_name, role, is_active FROM users;
