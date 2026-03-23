# Digi Payment Gateway - Implementation Phases Plan

This document provides a practical phase-by-phase implementation plan for the Digi Payment Gateway.

---

## Phase 1 - Project setup and dummy adapter

### Goal

Bootstrap the project and validate the end-to-end flow with a dummy adapter before integrating real payment channels.

### Scope

- Set up base Spring Boot project structure (api, service, domain, adapter, config, webhook).
- Configure environments (`dev`, `prod`) and application properties.
- Configure PostgreSQL, JPA
- Add shared `RestTemplate` bean configuration for outbound HTTP defaults (timeouts, interceptors, error handler).
- Create core entities/repositories with  required fields.
- Implement `PaymentChannelAdapter` contract.
- Add `DummyPaymentChannelAdapter` returning static/simulated responses.
- Wire adapter selection through orchestration service.
- Expose initial payment API endpoints with mock flow support.

### Deliverables

- Runnable service with health endpoint.
- DB schema initialized.
- Dummy adapter integrated and selectable by configuration.
- Basic API request/response flow working with test data.

---

## Phase 2 - Security code

### Goal

Implement authentication and authorization for system APIs and UI management operations.

### Scope

- API key authentication for server-to-server payment APIs.
- JWT authentication for login and UI/configuration endpoints.
- Spring Security filter chain separation by route patterns.
- Login endpoint and JWT token generation.
- Role/access checks for user-merchant mappings.
- Basic security hardening (CORS, headers, password hashing, token expiry).
- Audit/security logs for auth failures and token validation.

### Deliverables

- API key auth active on payment APIs.
- JWT auth active on user/UI configuration APIs.
- Unauthorized/forbidden handling and standardized error responses.

---

## Phase 3 - Adapter code for XplorPay

### Goal

Implement the first real payment channel adapter (XplorPay) as production reference.

### Scope

- Implement direct `RestTemplate.exchange(...)` calls inside `XplorPayAdapter` (no dedicated HTTP wrapper/facade layer).
- Apply shared bean defaults (timeouts, interceptors, error handler) and map transport errors to domain exceptions.
- Implement payment link creation request/response mapping.
- Implement webhook signature validation.
- Parse webhook payload into `PaymentStatusResponse`.
- Persist masked request/response logs in payment channel API log table.
- Integrate adapter with orchestration and webhook processor.
- Add integration test flow against sandbox/mock.

### Deliverables

- Working XplorPay payment link generation.
- Working XplorPay webhook validation and status update flow.
- Observability via channel API logging.

---

## Phase 4 - Adapter code for other channels

### Goal

Add additional channel adapters using the same contract and quality standards as XplorPay.

### Scope

- Implement Paymob adapter (2nd channel).
- Implement Stripe adapter (3rd channel).
- Implement Razorpay adapter (4th channel).
- Reuse the same direct `RestTemplate.exchange(...)` adapter pattern and shared bean configuration across channels.
- Add channel-specific webhook validation/parsing logic.
- Ensure dynamic channel resolution by merchant configuration.
- Add contract tests to guarantee adapter behavior consistency.

### Deliverables

- Multi-channel support with plug-and-play adapter selection.
- Channel onboarding checklist/template for future adapters.
- Stable, consistent behavior across all adapters.

---

## Phase 5 - User related code

### Goal

Implement user/account and user-merchant management capabilities for UI/admin operations.

### Scope

- Implement user registration.
- Implement login, profile, and password management APIs.
- Implement user-merchant mapping using `@ManyToMany` join table (`user_merchant`).
- Add verification state handling (`isVerified`) in user lifecycle.
- Implement user/merchant access control for configuration actions.
- Add admin endpoints for assigning/removing merchant access.
- Add tests for authorization boundaries (cross-merchant access prevention).

### Deliverables

- Complete user auth and access management module.
- Merchant-scoped authorization enforced across UI/config endpoints.
- Tested user management APIs.

---

## Suggested Execution Order

1. Phase 1 -> foundation and dummy flow
2. Phase 2 -> secure the foundation
3. Phase 3 -> first production adapter (XplorPay)
4. Phase 4 -> scale to additional channels
5. Phase 5 -> user and access management completion

---

## Notes

- Keep all adapter implementations behind the same interface contract.
- Standardize outbound calls on direct `RestTemplate` usage in adapters; avoid a separate HTTP wrapper layer.
- Reuse helper utilities where needed (masking, header builders, error mappers) without abstracting `RestTemplate` behind a facade.
- Add linting, tests, and logging in each phase to avoid late stabilization issues.

