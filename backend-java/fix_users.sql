USE codecognition_db;
DELETE FROM users;

INSERT INTO users (email, password, full_name, role, is_active) VALUES 
('admin@codecognition.ai', '$2a$10$SlT7NkJz.2qYNJv0h.XY/OKLDJNyZrDQ8HzKJx9j8wQHvLj9JgJTa', 'Admin User', 'ADMIN', TRUE),
('user@codecognition.ai', '$2a$10$gd5k8.VD/YsfV0Gvvh1yueYzYfYC1IbJsKtfRIhvp8fHzGzHjWy7C', 'Demo Customer', 'USER', TRUE);

SELECT id, email, full_name, role FROM users;
