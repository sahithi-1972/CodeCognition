USE codecognition_db;

INSERT INTO users (email, password, full_name, role, is_active) 
VALUES ('admin@codecognition.ai', '\\\.JJsDHzmIQVeUvMQe8s5UjGsZ516dm7m', 'Admin User', 'ADMIN', TRUE);

INSERT INTO users (email, password, full_name, role, is_active) 
VALUES ('user@codecognition.ai', '\\\', 'Demo Customer', 'USER', TRUE);

SELECT id, email, full_name, role FROM users;
