# OnDemand Service — Database

This document describes the **PostgreSQL** schema used by the ondemand-service application (`com.runalb.ondemand_service`). Tables are created and updated by Hibernate from JPA entities (`spring.jpa.hibernate.ddl-auto=update` in dev, `validate` in prod).

For application structure and API behavior, see [ARCHITECTURE.md](./ARCHITECTURE.md).

---

## Overview

| Item | Value |
|------|-------|
| Database | PostgreSQL |
| Dev database name | `db_ondemand_service` (see `application-dev.properties`) |
| ORM | Spring Data JPA / Hibernate |
| Schema management | Hibernate `ddl-auto` (no Flyway/Liquibase) |
| Auditing | `createdDateTime`, `updatedDateTime` on all entities via `AuditableEntity` |

### Naming conventions

- **Table names** are set explicitly on `@Table(name = "...")`.
- **Column names** use explicit `@Column(name = "...")` where defined on the entity; otherwise Spring Boot’s physical naming strategy maps Java camelCase fields to **snake_case** columns (e.g. `passwordHash` → `password_hash`).
- **Audit columns** are explicitly named `createdDateTime` and `updatedDateTime` (camelCase) on every entity extending `AuditableEntity`.

### Entity-relationship diagram

```mermaid
erDiagram
    users ||--o{ user_role : has
    roles ||--o{ user_role : assigned
    users ||--o{ user_merchant : linked
    merchant ||--o{ user_merchant : linked
    users ||--o{ user_business : linked
    business ||--o{ user_business : linked
    users ||--o| providers : owns
    users ||--o{ auth_refresh_token : has
    merchant ||--o| merchant_config : has
    merchant ||--o{ merchant_payment_channel_config : has
    catalog_category ||--o{ catalog_service : contains

    users {
        bigint id PK
        varchar email UK
        varchar mobile_number UK
        varchar password_hash
        varchar name
        boolean is_active
        boolean is_verified
    }

    roles {
        bigint id PK
        varchar role_name UK
        varchar description
    }

    merchant {
        bigint id PK
        varchar name
        varchar api_key UK
        varchar email UK
        boolean is_active
    }

    providers {
        bigint id PK
        bigint user_id UK,FK
        text bio
        boolean is_verified
        double average_rating
        int profile_completion_percentage
        boolean is_active
        text address
    }

    catalog_category {
        bigint id PK
        varchar name
        varchar description
        int display_order
        boolean active
    }

    catalog_service {
        bigint id PK
        bigint catalog_category_id FK
        varchar name
        varchar description
        int display_order
        boolean active
    }
```

---

## Shared audit columns

Every entity extends `AuditableEntity` and includes:

| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| `createdDateTime` | `timestamp` | `NOT NULL` | Set on insert; not updatable |
| `updatedDateTime` | `timestamp` | `NOT NULL` | Updated on each modification |

Populated by Spring Data JPA auditing (`@EnableJpaAuditing` on `OnDemandServiceApplication`).

---

## Tables

### `users`

Portal accounts. Passwords are stored as BCrypt hashes.

| Column | Type | Constraints | Entity field |
|--------|------|-------------|--------------|
| `id` | `bigint` | `PK`, identity | `id` |
| `email` | `varchar` | `NOT NULL`, `UNIQUE` | `email` |
| `mobile_number` | `varchar(20)` | `UNIQUE` | `mobileNumber` |
| `password_hash` | `varchar` | `NOT NULL` | `passwordHash` |
| `name` | `varchar` | `NOT NULL` | `name` |
| `is_active` | `boolean` | `NOT NULL`, default `true` | `isActive` |
| `is_verified` | `boolean` | `NOT NULL`, default `false` | `isVerified` |
| `createdDateTime` | `timestamp` | `NOT NULL` | audit |
| `updatedDateTime` | `timestamp` | `NOT NULL` | audit |

**Entity:** `UserEntity`

**Relationships:**

- M:N → `roles` via `user_role`
- M:N → `merchant` via `user_merchant`
- M:N → `business` via `user_business`
- 1:1 ← `providers` (`providers.user_id`)
- 1:N ← `auth_refresh_token` (`auth_refresh_token.user_id`)

**Common queries:** `findByEmail`, `findByMobileNumber`, `existsByMobileNumber`, `existsByIdAndMerchants_Id`, `existsByIdAndBusinesses_Id`

---

### `roles`

Lookup table for assignable roles. Rows must exist before user registration.

| Column | Type | Constraints | Entity field |
|--------|------|-------------|--------------|
| `id` | `bigint` | `PK`, identity | `id` |
| `role_name` | `varchar(64)` | `NOT NULL`, `UNIQUE` | `roleName` (`RoleNameEnum`, stored as string) |
| `description` | `varchar(255)` | | `description` |
| `createdDateTime` | `timestamp` | `NOT NULL` | audit |
| `updatedDateTime` | `timestamp` | `NOT NULL` | audit |

**Entity:** `RoleEntity`

**Enum values (`RoleNameEnum`):**

| Value | Description (seed) |
|-------|-------------------|
| `CUSTOMER` | End customer / marketplace buyer |
| `PROVIDER` | Service provider |
| `ADMIN` | Tenant or operations admin |
| `SUPER_ADMIN` | Full administrative access |

**Seed script:** `scripts/seed-roles.sql` (idempotent `ON CONFLICT (role_name) DO NOTHING`)

---

### `user_role`

Join table: users ↔ roles.

| Column | Type | Constraints |
|--------|------|-------------|
| `user_id` | `bigint` | `FK` → `users.id` |
| `role_id` | `bigint` | `FK` → `roles.id` |

Composite primary key on `(user_id, role_id)` (Hibernate default for `@JoinTable`).

---

### `user_merchant`

Join table: users ↔ merchants (portal ownership / access).

| Column | Type | Constraints |
|--------|------|-------------|
| `user_id` | `bigint` | `FK` → `users.id` |
| `merchant_id` | `bigint` | `FK` → `merchant.id` |

---

### `user_business`

Join table: users ↔ business records.

| Column | Type | Constraints |
|--------|------|-------------|
| `user_id` | `bigint` | `FK` → `users.id` |
| `business_id` | `bigint` | `FK` → `business.id` |

---

### `auth_refresh_token`

Opaque refresh tokens for session renewal. Only a **SHA-256 hash** of the token is stored.

| Column | Type | Constraints | Entity field |
|--------|------|-------------|--------------|
| `id` | `bigint` | `PK`, identity | `id` |
| `user_id` | `bigint` | `NOT NULL`, `FK` → `users.id` | `user` |
| `token_hash` | `varchar(64)` | `NOT NULL`, `UNIQUE` | `tokenHash` |
| `expires_at` | `timestamp` | `NOT NULL` | `expiresAt` |
| `revoked_at` | `timestamp` | nullable | `revokedAt` (set on logout / rotation) |
| `createdDateTime` | `timestamp` | `NOT NULL` | audit |
| `updatedDateTime` | `timestamp` | `NOT NULL` | audit |

**Entity:** `AuthRefreshTokenEntity`

**Common queries:** `findByTokenHashAndRevokedAtIsNull`

---

### `business`

Business tenant records. List/update/delete API endpoints are not fully implemented (`501`).

| Column | Type | Constraints | Entity field |
|--------|------|-------------|--------------|
| `id` | `bigint` | `PK`, identity | `id` |
| `name` | `varchar` | `NOT NULL` | `name` |
| `email` | `varchar` | `NOT NULL`, `UNIQUE` | `email` |
| `address` | `text` | | `address` |
| `mobile_number` | `varchar(20)` | `UNIQUE` | `mobileNumber` |
| `is_active` | `boolean` | `NOT NULL`, default `true` | `isActive` |
| `createdDateTime` | `timestamp` | `NOT NULL` | audit |
| `updatedDateTime` | `timestamp` | `NOT NULL` | audit |

**Entity:** `BusinessEntity`

**Relationships:** M:N ← `users` via `user_business`

**Common queries:** `findByEmail`

---

### `merchant`

Merchant accounts. Each row gets a unique `api_key` (UUID) on create for future integration authentication.

| Column | Type | Constraints | Entity field |
|--------|------|-------------|--------------|
| `id` | `bigint` | `PK`, identity | `id` |
| `name` | `varchar` | `NOT NULL` | `name` |
| `api_key` | `varchar` | `NOT NULL`, `UNIQUE` | `apiKey` |
| `email` | `varchar` | `NOT NULL`, `UNIQUE` | `email` |
| `is_active` | `boolean` | `NOT NULL`, default `true` | `isActive` |
| `createdDateTime` | `timestamp` | `NOT NULL` | audit |
| `updatedDateTime` | `timestamp` | `NOT NULL` | audit |

**Entity:** `MerchantEntity`

**Relationships:**

- 1:1 → `merchant_config`
- 1:N → `merchant_payment_channel_config`
- M:N ← `users` via `user_merchant`

**Common queries:** `findByApiKey`, `findByEmail`, `findByUsers_IdOrderByIdAsc`

---

### `merchant_config`

Per-merchant settings (webhook URL, default currency).

| Column | Type | Constraints | Entity field |
|--------|------|-------------|--------------|
| `id` | `bigint` | `PK`, identity | `id` |
| `merchant_id` | `bigint` | `NOT NULL`, `UNIQUE`, `FK` → `merchant.id` | `merchant` |
| `webhook_url` | `text` | | `webhookUrl` |
| `currency` | `varchar(3)` | `NOT NULL` | `currency` (ISO 4217, e.g. `USD`) |
| `createdDateTime` | `timestamp` | `NOT NULL` | audit |
| `updatedDateTime` | `timestamp` | `NOT NULL` | audit |

**Entity:** `MerchantConfigEntity`

One config row per merchant (`merchant_id` unique).

**Common queries:** `findByMerchant_Id`

---

### `merchant_payment_channel_config`

Per-merchant payment channel credentials/settings stored as opaque JSON. No `payment_channel` master table is wired in the current codebase (FK commented out in entity).

| Column | Type | Constraints | Entity field |
|--------|------|-------------|--------------|
| `id` | `bigint` | `PK`, identity | `id` |
| `merchant_id` | `bigint` | `NOT NULL`, `FK` → `merchant.id` | `merchant` |
| `is_active` | `boolean` | `NOT NULL`, default `true` | `isActive` |
| `config_json` | `text` | | `configJson` |
| `createdDateTime` | `timestamp` | `NOT NULL` | audit |
| `updatedDateTime` | `timestamp` | `NOT NULL` | audit |

**Entity:** `MerchantPaymentChannelConfigEntity`

**Common queries:** `findByMerchant_IdOrderByIdAsc`, `findByIdAndMerchant_Id`

> **Note:** `scripts/update-payment-channel-name-check.sql` references a legacy `payment_channel` table that is not present in current JPA entities.

---

### `providers`

Service provider profile extension; exactly one row per user with the `PROVIDER` role.

| Column | Type | Constraints | Entity field |
|--------|------|-------------|--------------|
| `id` | `bigint` | `PK`, identity | `id` |
| `user_id` | `bigint` | `NOT NULL`, `UNIQUE`, `FK` → `users.id` | `user` |
| `bio` | `text` | | `bio` |
| `is_verified` | `boolean` | `NOT NULL`, default `false` | `isVerified` |
| `average_rating` | `double precision` | `NOT NULL`, default `0.0` | `averageRating` |
| `profile_completion_percentage` | `integer` | `NOT NULL` | `profileCompletionPercentage` |
| `is_active` | `boolean` | `NOT NULL`, default `true` | `isActive` |
| `address` | `text` | | `address` |
| `createdDateTime` | `timestamp` | `NOT NULL` | audit |
| `updatedDateTime` | `timestamp` | `NOT NULL` | audit |

**Entity:** `ProviderEntity`

**Common queries:** `existsByUser_Id`, `findByIdWithUserAndRoles`, `findByUserIdWithUserAndRoles`

---

### `catalog_category`

Top-level catalog grouping for on-demand services.

| Column | Type | Constraints | Entity field |
|--------|------|-------------|--------------|
| `id` | `bigint` | `PK`, identity | `id` |
| `name` | `varchar(255)` | `NOT NULL` | `name` |
| `description` | `varchar(2000)` | | `description` |
| `display_order` | `integer` | `NOT NULL`, default `0` | `displayOrder` |
| `active` | `boolean` | `NOT NULL`, default `true` | `active` |
| `createdDateTime` | `timestamp` | `NOT NULL` | audit |
| `updatedDateTime` | `timestamp` | `NOT NULL` | audit |

**Entity:** `CatalogCategoryEntity`

**Common queries:** `findByActiveTrue`, `existsByNameIgnoreCase`

---

### `catalog_service`

Individual catalog entries under a category.

| Column | Type | Constraints | Entity field |
|--------|------|-------------|--------------|
| `id` | `bigint` | `PK`, identity | `id` |
| `catalog_category_id` | `bigint` | `NOT NULL`, `FK` → `catalog_category.id` | `catalogCategory` |
| `name` | `varchar(512)` | `NOT NULL` | `name` |
| `description` | `varchar(4000)` | | `description` |
| `display_order` | `integer` | `NOT NULL`, default `0` | `displayOrder` |
| `active` | `boolean` | `NOT NULL`, default `true` | `active` |
| `createdDateTime` | `timestamp` | `NOT NULL` | audit |
| `updatedDateTime` | `timestamp` | `NOT NULL` | audit |

**Entity:** `CatalogServiceEntity`

**Common queries:** `findByCatalogCategory_IdAndActiveTrueOrderByDisplayOrderAscIdAsc`, `existsByCatalogCategory_IdAndNameIgnoreCase`

---

## Data not persisted

The following application data is **in-memory only** and has no database tables:

| Data | Location | Notes |
|------|----------|-------|
| Email login OTP sessions | `AuthService` | Lost on restart; not cluster-safe |
| Forgot-password OTP sessions | `AuthService` | Same |
| Mobile login OTP sessions | `AuthService` | Same |

---

## Setup and operations

### Local development

1. Create PostgreSQL database `db_ondemand_service`.
2. Start the application with the `dev` profile (default). Hibernate creates/updates tables (`ddl-auto=update`).
3. Seed roles:

```bash
psql -h localhost -U postgres -d db_ondemand_service -f scripts/seed-roles.sql
```

### Production

- Set `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, and `JWT_SECRET`.
- Use `ddl-auto=validate` — schema must already match entities.
- Run `scripts/seed-roles.sql` once per environment before accepting user registrations.

### Inspecting schema

```sql
-- List tables
SELECT tablename FROM pg_tables WHERE schemaname = 'public' ORDER BY tablename;

-- Example: users table
\d users
```

With `spring.jpa.show-sql=true` (dev), Hibernate logs DDL and DML to the application log.

---

## Entity index

| Table | JPA entity | Package |
|-------|------------|---------|
| `users` | `UserEntity` | `user.entity` |
| `roles` | `RoleEntity` | `role.entity` |
| `user_role` | *(join table)* | — |
| `user_merchant` | *(join table)* | — |
| `user_business` | *(join table)* | — |
| `auth_refresh_token` | `AuthRefreshTokenEntity` | `auth.entity` |
| `business` | `BusinessEntity` | `business.entity` |
| `merchant` | `MerchantEntity` | `merchant.entity` |
| `merchant_config` | `MerchantConfigEntity` | `merchant.entity` |
| `merchant_payment_channel_config` | `MerchantPaymentChannelConfigEntity` | `merchant.entity` |
| `providers` | `ProviderEntity` | `provider.entity` |
| `catalog_category` | `CatalogCategoryEntity` | `catalog.entity` |
| `catalog_service` | `CatalogServiceEntity` | `catalog.entity` |

---

## Related documentation

| Document | Description |
|----------|-------------|
| [ARCHITECTURE.md](./ARCHITECTURE.md) | Application layers, security, API |
| [README.md](./README.md) | Documentation index |
| `scripts/seed-roles.sql` | Required role seed data |
| `scripts/update-payment-channel-name-check.sql` | Legacy `payment_channel` constraint fix (optional) |
