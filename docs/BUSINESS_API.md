# Business API (portal)

Base path: `/api/v1/portal/businesses`

All endpoints accept/return `application/json` unless noted.

## Authentication

All routes require **Bearer JWT** (`Authorization: Bearer <accessToken>`).

Routes with `{businessId}` additionally require the authenticated user to be linked to that business via `user_business` (`AuthService.assertAuthenticatedUserOwnsBusiness`).

`POST /` and `GET /` use the authenticated user from the JWT (no `businessId` in path).

## Endpoints

### Businesses

| # | Method | Path | Description | Status |
| - | ------ | ---- | ----------- | ------ |
| 1 | `POST` | `/` | Create business (links creator; returns `apiKey`) | 201 |
| 2 | `GET` | `/` | List businesses for authenticated user | 200 |
| 3 | `GET` | `/{businessId}` | Get business | 200 |
| 4 | `PATCH` | `/{businessId}` | Update business | 200 |
| 5 | `DELETE` | `/{businessId}` | Soft-delete business | 204 |

### Business config

| # | Method | Path | Description | Status |
| - | ------ | ---- | ----------- | ------ |
| 6 | `GET` | `/{businessId}/config` | Get config | 200 |
| 7 | `POST` | `/{businessId}/config` | Create config | 200 |
| 8 | `PATCH` | `/{businessId}/config` | Update config | 200 |
| 9 | `DELETE` | `/{businessId}/config` | Soft-delete config | 204 |

### Payment channel configs

| # | Method | Path | Description | Status |
| - | ------ | ---- | ----------- | ------ |
| 10 | `POST` | `/{businessId}/payment-channel-configs` | Create config | 201 |
| 11 | `GET` | `/{businessId}/payment-channel-configs` | List configs | 200 |
| 12 | `GET` | `/{businessId}/payment-channel-configs/{configId}` | Get config | 200 |
| 13 | `PATCH` | `/{businessId}/payment-channel-configs/{configId}` | Update config | 200 |
| 14 | `DELETE` | `/{businessId}/payment-channel-configs/{configId}` | Soft-delete config | 204 |

> `apiKey` on create is a UUID used for future integration auth (`X-API-Key` on `/api/v1/integration/**`).

---

## Request & response schemas

### `POST /`

**Request** (`BusinessCreateRequest`)

| Field | Type | Constraints |
| ----- | ---- | ----------- |
| `name` | string | required, max 255 chars |
| `email` | string | required, valid email, max 255 chars |

**Response** (`BusinessResponse`) — see [BusinessResponse](#businessresponse)

---

### `GET /`

**Response:** `BusinessResponse[]`

---

### `GET /{businessId}` · `PATCH /{businessId}`

**Path parameter:** `businessId` — owner only.

**`PATCH` request** (`BusinessUpdateRequest`) — at least one field required.

| Field | Type | Constraints |
| ----- | ---- | ----------- |
| `name` | string | max 255 chars |
| `email` | string | valid email, max 255 chars |

**Response** (`BusinessResponse`)

**Errors:** 400 empty body · 403 not owner · 404 not found · 409 email in use

---

### `DELETE /{businessId}`

Soft-deletes business (`isDeleted = true`).

**Response:** empty body (204 No Content)

---

### `GET /{businessId}/config`

**Response** (`BusinessConfigResponse`) — see [BusinessConfigResponse](#businessconfigresponse)

---

### `POST /{businessId}/config`

**Request** (`BusinessConfigCreateRequest`)

| Field | Type | Constraints |
| ----- | ---- | ----------- |
| `currency` | string | required, 3-letter ISO 4217 code (e.g. `USD`) |
| `webhookUrl` | string | optional |

**Response** (`BusinessConfigResponse`)

**Errors:** 409 config already exists

---

### `PATCH /{businessId}/config`

**Request** (`BusinessConfigUpdateRequest`) — all fields optional.

| Field | Type | Constraints |
| ----- | ---- | ----------- |
| `currency` | string | 3-letter ISO 4217 code |
| `webhookUrl` | string | |

**Response** (`BusinessConfigResponse`)

---

### `DELETE /{businessId}/config`

Soft-deletes config (`isDeleted = true`).

**Response:** empty body (204 No Content)

---

### `POST /{businessId}/payment-channel-configs`

**Request** (`BusinessPaymentChannelConfigCreateRequest`)

| Field | Type | Constraints |
| ----- | ---- | ----------- |
| `configJson` | string | optional, opaque JSON credentials/settings |

**Response** (`BusinessPaymentChannelConfigResponse`) — see [BusinessPaymentChannelConfigResponse](#businesspaymentchannelconfigresponse)

---

### `GET /{businessId}/payment-channel-configs`

**Response:** `BusinessPaymentChannelConfigResponse[]`

---

### `GET /{businessId}/payment-channel-configs/{configId}`

**Path parameters:** `businessId`, `configId`

**Response** (`BusinessPaymentChannelConfigResponse`)

---

### `PATCH /{businessId}/payment-channel-configs/{configId}`

**Request** (`BusinessPaymentChannelConfigUpdateRequest`) — at least one field required.

| Field | Type | Constraints |
| ----- | ---- | ----------- |
| `isDeleted` | boolean | optional |
| `configJson` | string | optional |

**Response** (`BusinessPaymentChannelConfigResponse`)

---

### `DELETE /{businessId}/payment-channel-configs/{configId}`

Soft-deletes payment channel config (`isDeleted = true`).

**Response:** empty body (204 No Content)

---

## Shared response types

### BusinessResponse

| Field | Type |
| ----- | ---- |
| `id` | number |
| `name` | string |
| `email` | string |
| `apiKey` | string |
| `isDeleted` | boolean |

### BusinessConfigResponse

| Field | Type |
| ----- | ---- |
| `businessId` | number |
| `currency` | string |
| `webhookUrl` | string \| null |

### BusinessPaymentChannelConfigResponse

| Field | Type |
| ----- | ---- |
| `id` | number |
| `businessId` | number |
| `configJson` | string \| null |

---

## Related documentation

| Document | Description |
| -------- | ----------- |
| [AUTH_API.md](./AUTH_API.md) | Obtain Bearer token |
| [ARCHITECTURE.md](./ARCHITECTURE.md) | Integration `X-API-Key` from `apiKey` |
| [DATABASE.md](./DATABASE.md) | `business`, `business_config`, `business_payment_channel_config` |
