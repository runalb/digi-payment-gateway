-- Seed roles for ondemand-service (PostgreSQL).
-- Matches RoleNameEnum: CUSTOMER, PROVIDER, ADMIN, SUPER_ADMIN.
--
-- Table: roles (see RoleEntity + AuditableEntity)
-- Expected columns: id, role_name, description, created_date_time, updated_date_time
-- (Spring default physical naming: camelCase -> snake_case in PostgreSQL.)
--
-- Run after the application or DDL has created the `roles` table, e.g.:
--   psql -h localhost -U postgres -d db_ondemand_service -f scripts/seed-roles.sql
--
-- Idempotent: skips rows that already exist on unique role_name.
-- If this still fails, inspect columns:  \d roles

INSERT INTO roles (role_name, description, created_date_time, updated_date_time)
VALUES
    ('CUSTOMER', 'End customer / marketplace buyer', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('PROVIDER', 'Service provider', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('ADMIN', 'Tenant or operations admin', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('SUPER_ADMIN', 'Full administrative access', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (role_name) DO NOTHING;
