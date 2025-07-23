-- Create database if not exists
CREATE DATABASE IF NOT EXISTS pms_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Use the database
USE pms_db;

-- Create default roles
INSERT IGNORE INTO role (id, name, description, created_at, updated_at) VALUES
(1, 'ADMIN', 'System Administrator', NOW(), NOW()),
(2, 'USER', 'Regular User', NOW(), NOW()),
(3, 'MANAGER', 'Project Manager', NOW(), NOW());

-- Create admin user (password: admin123)
INSERT IGNORE INTO user (id, email, password, name, nickname, email_verified, is_active, created_at, updated_at) VALUES
(1, 'admin@pms.com', '$2a$10$qWABpHE6F4P2Y8G.C7HfYOjqw5K2J8L9VKzXj1m2QCw.S8QxP2M8O', 'System Admin', 'Admin', true, true, NOW(), NOW());

-- Assign admin role to admin user
INSERT IGNORE INTO user_role (user_id, role_id) VALUES (1, 1);

-- Create sample labels colors
INSERT IGNORE INTO label (id, name, color, project_id, created_at, updated_at) VALUES
(1, 'Bug', '#FF0000', 1, NOW(), NOW()),
(2, 'Feature', '#00FF00', 1, NOW(), NOW()),
(3, 'Enhancement', '#0000FF', 1, NOW(), NOW()),
(4, 'Documentation', '#FFFF00', 1, NOW(), NOW()),
(5, 'Critical', '#FF00FF', 1, NOW(), NOW()); 