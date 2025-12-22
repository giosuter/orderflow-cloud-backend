-- V6__seed_orders_for_pagination.sql
-- Goal:
-- 1) Remove the initial demo orders from V2 (ORD-1001..ORD-1004)
-- 2) Insert 42 total orders for pagination testing

-- 1) Remove demo orders from V2 (safe even if they don't exist)
DELETE FROM orders
WHERE code IN ('ORD-1001','ORD-1002','ORD-1003','ORD-1004');

-- 2) Insert 42 orders total (we insert 42 here)
INSERT INTO orders (code, status, total, customer_name, description, created_at, updated_at)
VALUES
  ('ORD-1001', 'NEW',        99.90, 'Alice Example',      'Seed for pagination', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('ORD-1002', 'PAID',      149.50, 'Bob Example',        'Seed for pagination', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('ORD-1003', 'SHIPPED',   200.00, 'Carla Example',      'Seed for pagination', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('ORD-1004', 'CANCELLED',   0.00, 'David Example',      'Seed for pagination', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

  ('ORD-1005', 'PAID',       59.90, 'Eva Keller',         'Paid successfully',   CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('ORD-1006', 'NEW',       320.00, 'Fabian Schmid',      'Waiting for payment', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('ORD-1007', 'SHIPPED',   410.25, 'Giulia Bianchi',     'Shipped to customer', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('ORD-1008', 'PAID',       89.00, 'Hans Frei',          'Paid successfully',   CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('ORD-1009', 'NEW',       129.90, 'Irene Vogel',        'New order',           CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('ORD-1010', 'PAID',      999.99, 'Jonas Huber',        'High value order',    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('ORD-1011', 'CANCELLED',  45.00, 'Klara Steiner',      'Cancelled: OOS',      CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('ORD-1012', 'SHIPPED',   275.75, 'Luca Conti',         'Shipped',             CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('ORD-1013', 'PAID',      180.10, 'Marco Ferrari',      'Paid successfully',   CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('ORD-1014', 'NEW',        67.80, 'Nina Graf',          'New order',           CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('ORD-1015', 'SHIPPED',   510.00, 'Oliver Baumann',     'Shipped',             CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('ORD-1016', 'PAID',      120.00, 'Paola Ricci',        'Paid successfully',   CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('ORD-1017', 'NEW',        88.40, 'Quentin Morel',      'New order',           CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('ORD-1018', 'CANCELLED', 230.00, 'Rita Lombardi',      'Cancelled: payment',  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('ORD-1019', 'PAID',      305.60, 'Stefan Kunz',        'Paid successfully',   CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('ORD-1020', 'SHIPPED',    74.99, 'Tina Schneider',     'Shipped',             CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('ORD-1021', 'NEW',        19.99, 'Urs Berger',         'New order',           CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('ORD-1022', 'PAID',      420.00, 'Valentina Galli',    'Paid successfully',   CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('ORD-1023', 'CANCELLED',  60.00, 'Walter Hofer',       'Cancelled by cust.',  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('ORD-1024', 'SHIPPED',   150.00, 'Xenia Roth',         'Shipped',             CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('ORD-1025', 'NEW',       200.00, 'Yann Dubois',        'New order',           CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('ORD-1026', 'PAID',       99.99, 'Zoe Martin',         'Paid successfully',   CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('ORD-1027', 'SHIPPED',   330.00, 'Adrian Lutz',        'Shipped',             CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('ORD-1028', 'NEW',        15.50, 'Bianca Furrer',      'New order',           CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('ORD-1029', 'PAID',      275.00, 'Cedric Wyss',        'Paid successfully',   CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('ORD-1030', 'CANCELLED',  58.30, 'Doris Moser',        'Cancelled: duplicate',CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('ORD-1031', 'SHIPPED',   190.00, 'Elia Romano',        'Shipped',             CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('ORD-1032', 'NEW',        88.80, 'Florian Beck',       'New order',           CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('ORD-1033', 'PAID',      245.00, 'Greta Simon',        'Paid successfully',   CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('ORD-1034', 'SHIPPED',    39.90, 'Hugo Keller',        'Shipped',             CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('ORD-1035', 'NEW',       510.00, 'Isabel Fontana',     'New order',           CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('ORD-1036', 'PAID',       66.60, 'Julian Frei',        'Paid successfully',   CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('ORD-1037', 'SHIPPED',   129.00, 'Kevin Arnold',       'Shipped',             CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('ORD-1038', 'CANCELLED',  22.00, 'Laura Meier',        'Cancelled: test',     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('ORD-1039', 'PAID',      880.00, 'Michael Ziegler',    'Paid successfully',   CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('ORD-1040', 'NEW',        75.25, 'Nora Aeschlimann',   'New order',           CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('ORD-1041', 'PAID',       12.90, 'Oscar Reinhard',     'Paid successfully',   CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('ORD-1042', 'SHIPPED',   540.00, 'Patrizia Huber',     'Shipped',             CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);