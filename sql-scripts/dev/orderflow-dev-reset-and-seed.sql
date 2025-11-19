-- orderflow-dev-reset-and-seed.sql
-- Manual helper script for the dev DB (H2 or MariaDB).
-- Use this when you want to quickly reset and seed the orders table.

DELETE FROM orders;

INSERT INTO orders (code, status, total)
VALUES
    ('ORD-DEV-001', 'NEW',       50.00),
    ('ORD-DEV-002', 'NEW',       75.50),
    ('ORD-DEV-003', 'PAID',     120.00),
    ('ORD-DEV-004', 'SHIPPED',  200.00);
    

-- Usage:
-- For H2 in-memory dev: you normally don’t need this, because 
-- every restart + Flyway gives you a clean DB.    
-- 
-- For MariaDB dev/prod: paste/run this in phpMyAdmin or the SQL 
-- console if you want to reset demo data.    