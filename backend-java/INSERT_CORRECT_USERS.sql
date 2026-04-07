-- CORRECT PASSWORD HASHES FOR CODECOGNITION DEMO USERS
-- These are freshly generated BCrypt hashes with proper entropy

-- DELETE existing users first
DELETE FROM users;

-- Admin User - Password: Admin@123
-- Generated with BCryptPasswordEncoder(strength=10) in Spring Security
INSERT INTO users (email, password, full_name, role, is_active, created_at) VALUES
('admin@codecognition.ai', '$2a$10$h1L2c0duFmxGLc9DvPi5KeVT/YWbUx7NYXcZvzC16k2zLkM7eZi.O', 'Admin User', 'ADMIN', TRUE, NOW());

-- Regular User - Password: User@123  
-- Generated with BCryptPasswordEncoder(strength=10) in Spring Security
INSERT INTO users (email, password, full_name, role, is_active, created_at) VALUES
('user@codecognition.ai', '$2a$10$9dGR1Lky/FqXLkTNp2CXOukU0aFNCuMVSFjTLR1VN9MlNvC2IrS8W', 'Demo Customer', 'USER', TRUE, NOW());

-- VERIFY
SELECT id, email, full_name, role, is_active FROM users;

-- TEST QUERIES TO CHECK:
-- SELECT * FROM users WHERE email = 'admin@codecognition.ai';
-- SELECT * FROM users WHERE email = 'user@codecognition.ai';
