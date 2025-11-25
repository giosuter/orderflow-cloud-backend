-- V3__add_customer_name_to_orders.sql
-- Add customer_name column to orders table

ALTER TABLE orders
    ADD COLUMN customer_name VARCHAR(255);