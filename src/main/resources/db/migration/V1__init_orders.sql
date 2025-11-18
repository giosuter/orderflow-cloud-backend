-- Schema version: 1
-- Purpose: create initial table for Order aggregate

create table if not exists orders (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  code        varchar(64)   not null unique,
  total       decimal(14,2) not null,
  status      varchar(16)   not null,
  created_at  timestamp,
  updated_at  timestamp
);