-- Seed roles for ondemand-service (PostgreSQL).
-- Matches RoleNameEnum: CUSTOMER, PROVIDER, ADMIN, SUPER_ADMIN.
--
-- Table: roles (see RoleEntity + AuditableEntity)
-- Columns: id, role_name, description, "createdDateTime", "updatedDateTime"
-- Audit column names are camelCase per AuditableEntity @Column annotations.
--
-- Run after the application or DDL has created the `roles` table, e.g.:
--   psql -h localhost -U postgres -d db_ondemand_service -f scripts/seed-roles.sql
--
-- Idempotent: skips rows that already exist on unique role_name.
-- If this still fails, inspect columns:  \d roles

INSERT INTO roles (role_name, description, "createdDateTime", "updatedDateTime")
VALUES
    ('CUSTOMER', 'End customer / marketplace buyer', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('PROVIDER', 'Service provider', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('ADMIN', 'Tenant or operations admin', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('SUPER_ADMIN', 'Full administrative access', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (role_name) DO NOTHING;
