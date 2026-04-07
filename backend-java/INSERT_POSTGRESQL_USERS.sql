-- PostgreSQL SQL Script for CodeCognition Demo Users
-- Run this in your Render PostgreSQL database after it's created

-- Create users table if it doesn't exist
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100),
    role VARCHAR(50) NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP NULL,
    is_active BOOLEAN DEFAULT TRUE,
    CONSTRAINT chk_role CHECK (role IN ('USER', 'ADMIN'))
);

-- Create indexes
CREATE INDEX IF NOT EXISTS idx_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_role ON users(role);
CREATE INDEX IF NOT EXISTS idx_created_at ON users(created_at);
CREATE INDEX IF NOT EXISTS idx_is_active ON users(is_active);

-- Delete old demo users (if any)
DELETE FROM users WHERE email IN ('admin@codecognition.ai', 'user@codecognition.ai');

-- Insert Admin User - Password: Admin@123
INSERT INTO users (email, password, full_name, role, is_active, created_at) VALUES
('admin@codecognition.ai', '$2a$10$h1L2c0duFmxGLc9DvPi5KeVT/YWbUx7NYXcZvzC16k2zLkM7eZi.O', 'Admin User', 'ADMIN', TRUE, CURRENT_TIMESTAMP);

-- Insert Regular User - Password: User@123
INSERT INTO users (email, password, full_name, role, is_active, created_at) VALUES
('user@codecognition.ai', '$2a$10$9dGR1Lky/FqXLkTNp2CXOukU0aFNCuMVSFjTLR1VN9MlNvC2IrS8W', 'Demo Customer', 'USER', TRUE, CURRENT_TIMESTAMP);

-- Verify users were inserted
SELECT id, email, full_name, role, is_active FROM users;

-- Demo Credentials:
-- Admin: admin@codecognition.ai / Admin@123
-- User: user@codecognition.ai / User@123
