USE codecognition_db;

-- Clear all users
DELETE FROM users;

-- Insert admin user with verified bcrypt hash
-- Password: Admin@123
-- This hash is generated from: https://www.bcrypt-generator.com/ and verified
INSERT INTO users (email, password, full_name, role, is_active) 
VALUES ('admin@codecognition.ai', '$2a$10$SlT7NkJz.2qYNJv0h.XY/OKLDJNyZrDQ8HzKJx9j8wQHvLj9JgJTa', 'Admin User', 'ADMIN', TRUE);

-- Insert regular user with verified bcrypt hash  
-- Password: User@123
INSERT INTO users (email, password, full_name, role, is_active)
VALUES ('user@codecognition.ai', '$2a$10$gd5k8.VD/YsfV0Gvvh1yueYzYfYC1IbJsKtfRIhvp8fHzGzHjWy7C', 'Demo Customer', 'USER', TRUE);

-- Verify
SELECT id, email, full_name, role, is_active FROM users;
