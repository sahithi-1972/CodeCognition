-- Insert Demo Users into CodeCognition Database
-- Password hashing: These are BCrypt hashed passwords
-- Admin User: admin@codecognition.ai / Admin@123
-- Customer User: user@codecognition.ai / User@123

USE codecognition_db;

-- Clear existing users (optional, uncomment if needed)
-- DELETE FROM users;

-- Insert Admin User
INSERT INTO users (email, password, full_name, role, is_active) VALUES
('admin@codecognition.ai', '$2a$10$dXj3SW6G7P50eS6DQluzv.JJsDHzmIQVeUvMQe8s5UjGsZ516dm7m', 'Admin User', 'ADMIN', TRUE);

-- Insert Customer User
INSERT INTO users (email, password, full_name, role, is_active) VALUES
('user@codecognition.ai', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36gBUSPm', 'Demo Customer', 'USER', TRUE);

-- Verify insertion
SELECT id, email, full_name, role, is_active FROM users;

-- Note: Password hashes are for:
-- Admin: Admin@123
-- Customer: User@123
