# Digi Payment Gateway — Documentation

Backend: **ondemand-service** (`com.runalb.ondemand_service`) — Spring Boot REST API for auth, users, catalog, businesses, and business offerings.

## Contents

| Document | Description |
| -------- | ----------- |
| [ARCHITECTURE.md](./ARCHITECTURE.md) | Layers, security, domain model, and API endpoint reference |
| [DATABASE.md](./DATABASE.md) | PostgreSQL tables and relationships |
| [Postman collection](../postman/OnDemand-Service-API.postman_collection.json) | Manual API testing |

## Quick reference

| Area | Base path | Auth |
| ---- | --------- | ---- |
| Auth | `/api/v1/auth` | Public (`POST`) |
| Users | `/api/v1/users` | Registration public; profile JWT (owner) |
| Catalog | `/api/v1/catalog` | Read: any authenticated user; write: `SUPER_ADMIN` |
| Businesses | `/api/v1/businesses` | JWT + `PROVIDER` role; business ownership on `{businessId}` routes |
| Business offerings | `/api/v1/businesses/{businessId}/offerings` | Same as businesses |

See [ARCHITECTURE.md](./ARCHITECTURE.md) for security filters, authorization services, and implementation status.
