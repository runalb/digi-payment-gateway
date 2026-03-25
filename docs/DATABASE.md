# Digi Payment Gateway — Database Documentation

**Audience:** Database administrators, SRE, and backend engineers reviewing schema, backups, and migrations.

**Source of truth (application mapping):** JPA entities under `src/main/java/com/digirestro/digi_payment_gateway/entity/`.

**Target DBMS:** PostgreSQL (see `application-dev.properties` for connection settings).

---

## 1. Deployment and schema management

| Topic | Notes |
| ----- | ----- |
| **ORM** | Spring Data JPA / Hibernate |
| **Development** | `spring.jpa.hibernate.ddl-auto=update` may apply DDL at startup — convenient for dev, **not** a controlled migration for production. |
| **Physical column naming** | Spring Boot’s default Hibernate physical naming usually maps Java camelCase to **snake_case** (e.g. `apiKey` → `api_key`, `passwordHash` → `password_hash`) unless you override `spring.jpa.hibernate.naming.*`. **Exception:** `AuditableEntity` sets explicit names **`createdDateTime`** and **`updatedDateTime`** (see §3.1) — those columns are not snake_case. **Always validate** against `information_schema.columns` or Hibernate-exported DDL. |
| **Auditing** | `@EnableJpaAuditing` on `DigiPaymentGatewayApplication`; all entities extending `AuditableEntity` get the columns in §3.1. |

---

## 2. Entity–relationship diagram

### 2.1 Simple ER diagram

```mermaid
erDiagram
  users ||--o{ user_merchant : has_access_to
  merchant ||--o{ user_merchant : managed_by

  merchant ||--|| merchant_config : has_config
  merchant ||--o{ merchant_channel_config : channel_setup
  payment_channel ||--o{ merchant_channel_config : configured_for

  merchant ||--o{ payment : creates
  payment_channel ||--o{ payment : processes
  merchant_channel_config ||--o{ payment : uses_config

  payment ||--o{ webhook_incoming_log : receives
  payment ||--o{ webhook_merchant_log : notifies
  payment ||--o{ payment_channel_api_log : api_calls
```

### 2.2 Detailed ER diagram

High-level relationships (cardinality as enforced or implied by the JPA model):

```mermaid
erDiagram
  users ||--o{ user_merchant : "user_id"
  merchant ||--o{ user_merchant : "merchant_id"
  merchant ||--|| merchant_config : "1_to_1"
  merchant ||--o{ merchant_channel_config : "merchant_id"
  payment_channel ||--o{ merchant_channel_config : "payment_channel_id"
  merchant ||--o{ payment : "merchant_id"
  merchant_channel_config ||--o{ payment : "channel_config_id"
  payment_channel ||--o{ payment : "payment_channel_id"
  payment ||--o{ webhook_incoming_log : "payment_id"
  payment_channel ||--o{ webhook_incoming_log : "payment_channel_id"
  payment ||--o{ webhook_merchant_log : "payment_id"
  payment_channel ||--o{ webhook_merchant_log : "payment_channel_id"
  webhook_incoming_log ||--o{ webhook_merchant_log : "webhook_incoming_log_id"
  payment ||--o{ payment_channel_api_log : "payment_id"
  payment_channel ||--o{ payment_channel_api_log : "payment_channel_id"
  merchant_channel_config ||--o{ payment_channel_api_log : "channel_config_id"

  users {
    bigint id PK
    string email UK
    string password_hash
    string name
    boolean is_active
    boolean is_verified
    timestamp createdDateTime
    timestamp updatedDateTime
  }

  merchant {
    bigint id PK
    string name
    string api_key UK
    boolean is_active
    timestamp createdDateTime
    timestamp updatedDateTime
  }

  user_merchant {
    bigint user_id PK
    bigint merchant_id PK
  }

  merchant_config {
    bigint id PK
    bigint merchant_id FK
    text webhook_url
    varchar currency
    timestamp createdDateTime
    timestamp updatedDateTime
  }

  payment_channel {
    bigint id PK
    varchar name UK
    boolean is_active
    timestamp createdDateTime
    timestamp updatedDateTime
  }

  merchant_channel_config {
    bigint id PK
    bigint merchant_id FK
    bigint payment_channel_id FK
    boolean is_active
    text config_json
    timestamp createdDateTime
    timestamp updatedDateTime
  }

  payment {
    bigint id PK
    bigint merchant_id FK
    bigint channel_config_id FK
    bigint payment_channel_id FK
    string merchant_reference_payment_id
    string payment_channel_txn_id
    numeric amount
    varchar currency
    varchar status
    string payment_link_url
    text merchant_metadata_json
    timestamp createdDateTime
    timestamp updatedDateTime
  }

  webhook_incoming_log {
    bigint id PK
    bigint payment_id FK
    bigint payment_channel_id FK
    text raw_payload
    string status
    timestamp createdDateTime
    timestamp updatedDateTime
  }

  webhook_merchant_log {
    bigint id PK
    bigint webhook_incoming_log_id FK
    bigint payment_id FK
    bigint payment_channel_id FK
    string webhook_url
    text payload
    string status
    integer retry_count
    timestamp last_attempt_at
    timestamp createdDateTime
    timestamp updatedDateTime
  }

  payment_channel_api_log {
    bigint id PK
    bigint payment_id FK
    bigint payment_channel_id FK
    bigint channel_config_id FK
    string operation
    string request_method
    string request_url
    text request_headers
    text request_body
    int response_status_code
    text response_headers
    text response_body
    int duration_ms
    timestamp createdDateTime
    timestamp updatedDateTime
  }
```

### 2.3 Relationship summary

| From | To | Cardinality | Implementation notes |
| ---- | -- | ----------- | -------------------- |
| `users` | `merchant` | **M:N** | Join table `user_merchant` (`user_id`, `merchant_id`). Composite PK recommended for DBA control (JPA may not declare PK on join table — verify DDL). |
| `merchant` | `merchant_config` | **1:0..1** | `merchant_config.merchant_id` **UNIQUE** + NOT NULL → at most one config row per merchant. |
| `merchant` | `merchant_channel_config` | **1:N** | Multiple channel configs per merchant (e.g. per `payment_channel`). |
| `payment_channel` | `merchant_channel_config` | **1:N** | Same channel can be configured for many merchants. |
| `merchant` | `payment` | **1:N** | |
| `merchant_channel_config` | `payment` | **1:N** | |
| `payment_channel` | `payment` | **1:N** | `payment.payment_channel_id` references the channel row; channel key is `payment_channel.name` (enum string), not duplicated on `payment`. |
| `payment` | `webhook_incoming_log` | **1:N** | `payment_id` nullable on entity → optional FK in DB. |
| `payment` | `webhook_merchant_log` | **1:N** | `payment_id` NOT NULL. |
| `webhook_incoming_log` | `webhook_merchant_log` | **1:N** | `webhook_incoming_log_id` nullable. |
| `payment` | `payment_channel_api_log` | **1:N** | `payment_id` nullable. |

---

## 3. Table specifications

Naming below: non-audit fields use **snake_case** as typically produced by Spring Boot’s default physical naming. **Audit** columns are the exception (see §3.1).

### 3.1 Auditing columns (inherited)

Present on every entity that extends `AuditableEntity` (`UserEntity`, `MerchantEntity`, `MerchantConfigEntity`, `PaymentChannelEntity`, `MerchantChannelConfigEntity`, `PaymentEntity`, `WebhookIncomingLogEntity`, `WebhookMerchantLogEntity`, `PaymentChannelApiLogEntity`). Mapped in `AuditableEntity` with explicit `@Column` names:

| Column | Type | Nullable | Description |
| ------ | ---- | -------- | ----------- |
| `createdDateTime` | `timestamp` | NOT NULL | Set on insert (`@CreatedDate`). |
| `updatedDateTime` | `timestamp` | NOT NULL | Updated on each change (`@LastModifiedDate`). |

On PostgreSQL, Hibernate typically emits quoted identifiers for these names so casing matches the mapping. Per-table “+ audit” rows in §3.2–§3.11 refer to these two columns.

---

### 3.2 `users`

Back-office / UI users; many-to-many with merchants.

| Column | Type | Constraints | Description |
| ------ | ---- | ----------- | ----------- |
| `id` | `bigint` | PK, identity | Surrogate key. |
| `email` | `varchar` | NOT NULL, UNIQUE | Login identifier. |
| `password_hash` | `varchar` | NOT NULL | Password storage (e.g. BCrypt). |
| `name` | `varchar` | NOT NULL | Display name. |
| `is_active` | `boolean` | NOT NULL | Account enabled. |
| `is_verified` | `boolean` | NOT NULL | Verification flag. |
| + audit | `createdDateTime`, `updatedDateTime` | NOT NULL | §3.1 |

**Indexes (recommended):** PK on `id`, UNIQUE on `email`.

---

### 3.3 `user_merchant`

Join table for `users` ↔ `merchant`.

| Column | Type | Constraints | Description |
| ------ | ---- | ----------- | ----------- |
| `user_id` | `bigint` | FK → `users.id` | |
| `merchant_id` | `bigint` | FK → `merchant.id` | |

**Indexes (recommended):** Composite PK `(user_id, merchant_id)`; index on `merchant_id` for reverse lookups.

---

### 3.4 `merchant`

Core merchant record; API key for server-to-server integration.

| Column | Type | Constraints | Description |
| ------ | ---- | ----------- | ----------- |
| `id` | `bigint` | PK, identity | |
| `name` | `varchar` | NOT NULL | |
| `api_key` | `varchar` | NOT NULL, UNIQUE | Merchant API key (e.g. UUID string). |
| `is_active` | `boolean` | NOT NULL | |
| + audit | `createdDateTime`, `updatedDateTime` | NOT NULL | §3.1 |

**Indexes:** PK, UNIQUE(`api_key`).

---

### 3.5 `merchant_config`

**One row per merchant** (integration defaults and callbacks).

| Column | Type | Constraints | Description |
| ------ | ---- | ----------- | ----------- |
| `id` | `bigint` | PK, identity | |
| `merchant_id` | `bigint` | NOT NULL, FK → `merchant.id`, **UNIQUE** | Enforces 1:1. |
| `webhook_url` | `text` | NULL | Consumer webhook URL for outbound notifications. |
| `currency` | `varchar(3)` | NOT NULL | ISO 4217 alphabetic code (e.g. `USD`). Used when creating payments / links. |
| + audit | `createdDateTime`, `updatedDateTime` | NOT NULL | §3.1 |

**Indexes:** UNIQUE(`merchant_id`); FK to `merchant`.

---

### 3.6 `payment_channel`

Catalog of integrated payment providers. Mapped by **`PaymentChannelEntity`**, which **extends `AuditableEntity`** (same audit columns as §3.1).

| Column | Type | Constraints | Description |
| ------ | ---- | ----------- | ----------- |
| `id` | `bigint` | PK, identity | |
| `name` | `varchar` | NOT NULL, UNIQUE | Enum string — see §4.1. |
| `is_active` | `boolean` | NOT NULL | Whether this channel is available for new routing / configuration. |
| + audit | `createdDateTime`, `updatedDateTime` | NOT NULL | §3.1 |

---

### 3.7 `merchant_channel_config`

Per-merchant, per-channel credentials and settings (often JSON).

| Column | Type | Constraints | Description |
| ------ | ---- | ----------- | ----------- |
| `id` | `bigint` | PK, identity | |
| `merchant_id` | `bigint` | NOT NULL, FK → `merchant.id` | |
| `payment_channel_id` | `bigint` | NOT NULL, FK → `payment_channel.id` | |
| `is_active` | `boolean` | NOT NULL | Only active configs should be used by orchestration. |
| `config_json` | `text` | NULL | Channel-specific secrets/config (protect at rest). |
| + audit | `createdDateTime`, `updatedDateTime` | NOT NULL | §3.1 |

**Indexes (recommended):** `(merchant_id, is_active)` for “resolve active config” queries; FKs.

---

### 3.8 `payment`

Payment attempt / transaction record.

| Column | Type | Constraints | Description |
| ------ | ---- | ----------- | ----------- |
| `id` | `bigint` | PK, identity | Internal payment id (exposed to consumers as appropriate). |
| `merchant_id` | `bigint` | NOT NULL, FK → `merchant.id` | |
| `channel_config_id` | `bigint` | NOT NULL, FK → `merchant_channel_config.id` | |
| `payment_channel_id` | `bigint` | NOT NULL, FK → `payment_channel.id` | Channel key is **`payment_channel.name`** (see §4.1); there is no separate name column on `payment`. |
| `merchant_reference_payment_id` | `varchar` | NOT NULL | Idempotent / correlation id from consumer. |
| `payment_channel_txn_id` | `varchar` | NULL | Provider transaction id after link creation / updates. |
| `amount` | `numeric(19,4)` | NOT NULL | |
| `currency` | `varchar(3)` | NOT NULL | Copied from `merchant_config.currency` at creation (not from raw API body in current design). |
| `status` | `varchar` | NOT NULL | Enum string — see §4. Default `PENDING`. |
| `payment_link_url` | `varchar` | NULL | Generated checkout URL. |
| `merchant_metadata_json` | `text` | NULL | Opaque merchant metadata. |
| + audit | `createdDateTime`, `updatedDateTime` | NOT NULL | §3.1 |

**Indexes (recommended):** FK indexes; optional UNIQUE(`merchant_id`, `merchant_reference_payment_id`) if business rules require global idempotency per merchant.

---

### 3.9 `webhook_incoming_log`

Inbound webhook payload audit from payment channels.

| Column | Type | Constraints | Description |
| ------ | ---- | ----------- | ----------- |
| `id` | `bigint` | PK, identity | |
| `payment_id` | `bigint` | FK → `payment.id`, NULL | Optional link to payment. |
| `payment_channel_id` | `bigint` | FK → `payment_channel.id`, NULL | |
| `raw_payload` | `text` | NOT NULL | Raw body. |
| `status` | `varchar` | NOT NULL | Application-defined processing status. |
| + audit | `createdDateTime`, `updatedDateTime` | NOT NULL | §3.1 |

---

### 3.10 `webhook_merchant_log`

Outbound calls to the consumer webhook URL (and retries).

| Column | Type | Constraints | Description |
| ------ | ---- | ----------- | ----------- |
| `id` | `bigint` | PK, identity | |
| `webhook_incoming_log_id` | `bigint` | FK → `webhook_incoming_log.id`, NULL | |
| `payment_id` | `bigint` | NOT NULL, FK → `payment.id` | |
| `payment_channel_id` | `bigint` | NOT NULL, FK → `payment_channel.id` | |
| `webhook_url` | `varchar` | NOT NULL | URL used for this attempt (copy at send time). |
| `payload` | `text` | NOT NULL | JSON (or similar) sent to consumer. |
| `status` | `varchar` | NOT NULL | e.g. PENDING / SUCCESS / FAILED. |
| `retry_count` | `integer` | NOT NULL | Default 0. |
| `last_attempt_at` | `timestamp` | NULL | |
| + audit | `createdDateTime`, `updatedDateTime` | NOT NULL | §3.1 |

---

### 3.11 `payment_channel_api_log`

Outbound HTTP audit to payment channel APIs.

| Column | Type | Constraints | Description |
| ------ | ---- | ----------- | ----------- |
| `id` | `bigint` | PK, identity | |
| `payment_id` | `bigint` | FK → `payment.id`, NULL | |
| `payment_channel_id` | `bigint` | FK → `payment_channel.id`, NULL | |
| `channel_config_id` | `bigint` | FK → `merchant_channel_config.id`, NULL | |
| `operation` | `varchar` | NOT NULL | e.g. `CREATE_PAYMENT_LINK`. |
| `request_method` | `varchar` | NOT NULL | GET, POST, … |
| `request_url` | `varchar` | NOT NULL | |
| `request_headers` | `text` | NULL | Mask secrets in application layer. |
| `request_body` | `text` | NULL | |
| `response_status_code` | `integer` | NULL | |
| `response_headers` | `text` | NULL | |
| `response_body` | `text` | NULL | |
| `duration_ms` | `integer` | NULL | |
| + audit | `createdDateTime`, `updatedDateTime` | NOT NULL | §3.1 |

**Retention:** Log tables can grow quickly — define **retention/archival** policy (partitioning by month, TTL job, etc.).

---

## 4. Enumerated values (application layer)

Stored as **strings** in VARCHAR columns (`EnumType.STRING`).

### 4.1 `payment_channel.name` — `PaymentChannelNameEnum`

`XPLORPAY`, `PAYMOB`, `STRIPE`, `RAZORPAY`, `TEST`

If the database was created with an older CHECK constraint (for example only `DUMMY` or without `TEST`), manual inserts or Hibernate may fail with:

`violates check constraint "payment_channel_name_check"`.

Reconcile the constraint with the enum by running:

`scripts/sql/postgresql/update-payment-channel-name-check.sql`

If any row still uses `DUMMY`, update it to `TEST` before applying the script (see comment in that file).

### 4.2 `payment.status` — `PaymentStatusEnum`

`PENDING`, `SUCCESS`, `FAILED`, `CANCELLED`, `EXPIRED`

---

## 5. Operational checklist for DBAs

1. **FK consistency:** Nullable FKs on log tables allow partial records; monitor orphan rates if you add NOT NULL constraints later.
2. **1:1 enforcement:** Rely on **UNIQUE** constraint on `merchant_config.merchant_id` (duplicate rows must fail).
3. **Currency:** `merchant_config.currency` and `payment.currency` should stay aligned with product rules (ISO 4217).

---

## 6. Document history

| Version | Date | Author / note | Changes |
| ------- | ---- | ------------- | ------- |
| 1.0 | 2025-03-23 | Engineering | Initial DBA-oriented schema doc from JPA entities. |
| 1.1 | 2025-03-23 | Engineering | `payment_channel`: `PaymentChannelEntity` extends `AuditableEntity`; document `is_active` and audit columns. |
| 1.2 | 2026-03-25 | Engineering | Align with JPA: audit columns **`createdDateTime`** / **`updatedDateTime`** (`AuditableEntity`); remove obsolete `payment.payment_channel_name`; ER diagram and §1 naming notes; `@EnableJpaAuditing` reference. |
