-- Adds a persistent description field to orders.
-- This is required because the frontend sends "description" and we want it stored in DB.

ALTER TABLE orders
  ADD COLUMN description VARCHAR(2000) NULL;