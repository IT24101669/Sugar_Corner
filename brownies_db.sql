-- ==========================================
-- File: brownies_db.sql
-- Database: MySQL Workbench
-- Username: root | Password: ms1212#
-- Purpose: Create and seed the Brownie Shop Order Management database
-- Run this BEFORE starting the Spring Boot application
-- ==========================================

-- Create the database
CREATE DATABASE IF NOT EXISTS brownies_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE brownies_db;

-- ==========================================
-- TABLE: users
-- Stores both customers and admins
-- ==========================================
CREATE TABLE IF NOT EXISTS users (
    id          BIGINT       AUTO_INCREMENT PRIMARY KEY,
    full_name   VARCHAR(100) NOT NULL,
    email       VARCHAR(150) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,           -- BCrypt hashed
    phone       VARCHAR(20),
    address     VARCHAR(300),
    role        VARCHAR(30)  NOT NULL,           -- ROLE_CUSTOMER or ROLE_ADMIN
    active      TINYINT(1)   NOT NULL DEFAULT 1,
    created_at  DATETIME     DEFAULT CURRENT_TIMESTAMP
);

-- ==========================================
-- TABLE: orders
-- Stores each customer order
-- ==========================================
CREATE TABLE IF NOT EXISTS orders (
    id               BIGINT         AUTO_INCREMENT PRIMARY KEY,
    customer_id      BIGINT         NOT NULL,
    status           VARCHAR(30)    NOT NULL DEFAULT 'PENDING',
    order_type       VARCHAR(20)    NOT NULL,     -- DELIVERY or PICKUP
    delivery_address VARCHAR(300),
    total_amount     DECIMAL(10,2),
    customer_note    TEXT,
    admin_note       TEXT,
    requested_time   DATETIME,
    notified         TINYINT(1)     NOT NULL DEFAULT 0,  -- For US-8 notifications
    created_at       DATETIME       DEFAULT CURRENT_TIMESTAMP,
    updated_at       DATETIME       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_orders_customer
        FOREIGN KEY (customer_id) REFERENCES users(id)
        ON DELETE CASCADE
);

-- ==========================================
-- TABLE: order_items
-- Line items for each order
-- ==========================================
CREATE TABLE IF NOT EXISTS order_items (
    id            BIGINT        AUTO_INCREMENT PRIMARY KEY,
    order_id      BIGINT        NOT NULL,
    product_name  VARCHAR(200)  NOT NULL,
    quantity      INT           NOT NULL DEFAULT 1,
    unit_price    DECIMAL(10,2) NOT NULL,
    customization VARCHAR(300),
    subtotal      DECIMAL(10,2),

    CONSTRAINT fk_items_order
        FOREIGN KEY (order_id) REFERENCES orders(id)
        ON DELETE CASCADE
);

-- ==========================================
-- SEED DATA: Admin account
-- Password: admin123 (BCrypt encoded)
-- Login: admin@brownies.com / admin123
-- ==========================================
INSERT INTO users (full_name, email, password, phone, address, role, active)
VALUES (
    'Shop Admin',
    'admin@brownies.com',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LPVrmHUlk42', -- BCrypt of 'admin123'
    '+94 77 000 0001',
    'Brownie Shop, Main Street, Colombo',
    'ROLE_ADMIN',
    1
);

-- ==========================================
-- SEED DATA: Sample customer accounts
-- Password for all: customer123
-- ==========================================
INSERT INTO users (full_name, email, password, phone, address, role, active)
VALUES
(
    'Ayesha Perera',
    'ayesha@gmail.com',
    '$2a$10$8Wr0.C5XfrZ7ygW6fxiGseFiFTaRG.1oEEq.67qkwXKc2qT4dB3p6', -- customer123
    '+94 77 123 4567',
    '45 Galle Road, Colombo 03',
    'ROLE_CUSTOMER',
    1
),
(
    'Kasun Fernando',
    'kasun@gmail.com',
    '$2a$10$8Wr0.C5XfrZ7ygW6fxiGseFiFTaRG.1oEEq.67qkwXKc2qT4dB3p6', -- customer123
    '+94 71 987 6543',
    '12 Temple Road, Kandy',
    'ROLE_CUSTOMER',
    1
),
(
    'Nimali Silva',
    'nimali@gmail.com',
    '$2a$10$8Wr0.C5XfrZ7ygW6fxiGseFiFTaRG.1oEEq.67qkwXKc2qT4dB3p6', -- customer123
    '+94 76 555 7890',
    '78 Baseline Road, Colombo 09',
    'ROLE_CUSTOMER',
    1
);

-- ==========================================
-- SEED DATA: Sample orders for testing
-- ==========================================

-- Order 1: Active order for Ayesha (CONFIRMED)
INSERT INTO orders (customer_id, status, order_type, delivery_address, total_amount, customer_note, requested_time, notified)
VALUES (2, 'CONFIRMED', 'DELIVERY', '45 Galle Road, Colombo 03', 1030.00, 'Please use extra packaging', DATE_ADD(NOW(), INTERVAL 2 HOUR), 1);

INSERT INTO order_items (order_id, product_name, quantity, unit_price, customization, subtotal)
VALUES
(1, 'Classic Chocolate Brownie', 2, 350.00, 'no walnuts', 700.00),
(1, 'Salted Caramel Brownie', 1, 550.00, NULL, 550.00);

-- Update total for order 1
UPDATE orders SET total_amount = 1250.00 WHERE id = 1;

-- Order 2: In preparation order for Kasun
INSERT INTO orders (customer_id, status, order_type, delivery_address, total_amount, customer_note, admin_note, requested_time, notified)
VALUES (3, 'IN_PREPARATION', 'DELIVERY', '12 Temple Road, Kandy', 960.00, 'Birthday gift — please add a note', 'Extra packaging requested — fragile delivery', DATE_ADD(NOW(), INTERVAL 3 HOUR), 1);

INSERT INTO order_items (order_id, product_name, quantity, unit_price, customization, subtotal)
VALUES
(2, 'Nutella Swirl Brownie', 1, 580.00, NULL, 580.00),
(2, 'Double Fudge Brownie', 1, 420.00, 'extra fudge', 420.00);

UPDATE orders SET total_amount = 1000.00 WHERE id = 2;

-- Order 3: Pending pickup order for Nimali
INSERT INTO orders (customer_id, status, order_type, total_amount, customer_note, notified)
VALUES (4, 'PENDING', 'PICKUP', 870.00, 'I am allergic to peanuts', 0);

INSERT INTO order_items (order_id, product_name, quantity, unit_price, customization, subtotal)
VALUES
(3, 'Walnut Brownie', 1, 400.00, 'NO peanuts or nut oils', 400.00),
(3, 'Cream Cheese Brownie', 1, 480.00, NULL, 480.00);

UPDATE orders SET total_amount = 880.00 WHERE id = 3;

-- Order 4: Delivered order for Ayesha (historical)
INSERT INTO orders (customer_id, status, order_type, delivery_address, total_amount, notified, created_at)
VALUES (2, 'DELIVERED', 'DELIVERY', '45 Galle Road, Colombo 03', 1800.00, 1, DATE_SUB(NOW(), INTERVAL 2 DAY));

INSERT INTO order_items (order_id, product_name, quantity, unit_price, customization, subtotal)
VALUES (4, 'Brownie Box (6pcs)', 1, 1800.00, 'Assorted mix', 1800.00);

UPDATE orders SET total_amount = 1800.00 WHERE id = 4;

-- ==========================================
-- VERIFICATION QUERIES (run to confirm setup)
-- ==========================================
-- SELECT id, full_name, email, role FROM users;
-- SELECT id, customer_id, status, order_type, total_amount FROM orders;
-- SELECT id, order_id, product_name, quantity, unit_price FROM order_items;

-- ==========================================
-- USEFUL ADMIN QUERIES FOR REFERENCE
-- ==========================================

-- US-4: View all orders with customer info
-- SELECT o.id, u.full_name, u.email, o.status, o.order_type, o.total_amount, o.created_at
-- FROM orders o JOIN users u ON o.customer_id = u.id
-- ORDER BY o.created_at DESC;

-- US-9: Filter by date
-- SELECT * FROM orders WHERE DATE(created_at) = '2024-01-15';

-- US-8: Count unnotified orders
-- SELECT COUNT(*) FROM orders WHERE notified = 0;

-- ==========================================
-- TO RESET EVERYTHING (careful!):
-- DROP DATABASE brownies_db;
-- Then re-run this script.
-- ==========================================

SELECT 'Database setup complete! ✓' AS message;
SELECT CONCAT('Users: ', COUNT(*)) AS info FROM users
UNION ALL
SELECT CONCAT('Orders: ', COUNT(*)) FROM orders
UNION ALL
SELECT CONCAT('Order Items: ', COUNT(*)) FROM order_items;
