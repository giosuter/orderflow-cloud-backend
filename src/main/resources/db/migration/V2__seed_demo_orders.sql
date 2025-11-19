-- V2__seed_demo_orders.sql
-- Demo data for OrderFlow dev/test environment.
-- Keep data simple and neutral – useful to show CRUD to customers/headhunters.

INSERT INTO orders (code, status, total)
VALUES
    ('ORD-1001', 'NEW',       99.90),
    ('ORD-1002', 'PAID',     149.50),
    ('ORD-1003', 'SHIPPED',  200.00),
    ('ORD-1004', 'CANCELLED',  0.00);

-- Extend with more test orders later, but keep the volume small to 
-- make debugging and demos easy.
-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
-- When you run the app (dev profile with H2), Flyway will execute V1, 
-- then V2, and you will see these rows in the orders table automatically.    