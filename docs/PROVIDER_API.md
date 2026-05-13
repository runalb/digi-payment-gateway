# Provider API

Base path: `/api/v1/providers`

All endpoints accept/return `application/json` unless noted.

## Authentication

All routes require **Bearer JWT** with role **`PROVIDER`** (`Authorization: Bearer <accessToken>`).

| Endpoint | Extra authorization |
| -------- | ------------------- |
| `GET /` | `PROVIDER` role only |
| `POST /` | `PROVIDER` role; uses authenticated user as profile owner |
| `GET /{providerId}` | `PROVIDER` role; service enforces profile owner |
| `PUT /{providerId}` | `PROVIDER` role + `assertAuthenticatedUserOwnsProvider` |
| `DELETE /{providerId}` | `PROVIDER` role + `assertAuthenticatedUserOwnsProvider` |

---

## Endpoints

| # | Method | Path | Description | Status |
| - | ------ | ---- | ----------- | ------ |
| 1 | `GET` | `/` | List all active provider profiles | 200 |
| 2 | `GET` | `/{providerId}` | Get provider profile (owner only) | 200 |
| 3 | `POST` | `/` | Create provider profile for authenticated user | 201 |
| 4 | `PUT` | `/{providerId}` | Update profile (owner only) | 200 |
| 5 | `DELETE` | `/{providerId}` | Deactivate profile (owner only) | 204 |

---

## Request & response schemas

### `GET /`

**Response:** `ProviderDetailResponse[]` — see [ProviderDetailResponse](#providerdetailresponse)

Returns all providers where `isActive = true`.

---

### `GET /{providerId}`

**Path parameter:** `providerId`

**Response** (`ProviderDetailResponse`)

**Errors**

| Status | When |
| ------ | ---- |
| 403 | Authenticated user is not the profile owner |
| 404 | Provider not found |

---

### `POST /`

**Request** (`ProviderCreateRequest`)

| Field | Type | Constraints |
| ----- | ---- | ----------- |
| `bio` | string | optional, max 8000 chars |
| `address` | string | optional, max 2000 chars |

**Response** (`ProviderDetailResponse`)

**Errors**

| Status | When |
| ------ | ---- |
| 409 | Provider profile already exists for this user |

One provider profile per user.

---

### `PUT /{providerId}`

**Path parameter:** `providerId` — owner only.

**Request** (`ProviderUpdateRequest`) — null fields are left unchanged.

| Field | Type | Constraints |
| ----- | ---- | ----------- |
| `bio` | string | max 8000 chars |
| `address` | string | max 2000 chars |

**Response** (`ProviderDetailResponse`)

**Errors:** 403 not owner · 404 not found

---

### `DELETE /{providerId}`

**Path parameter:** `providerId` — owner only.

Sets `isActive = false` (soft deactivate).

**Response:** empty body (204 No Content)

---

## Shared response type

### ProviderDetailResponse

| Field | Type |
| ----- | ---- |
| `providerId` | number |
| `bio` | string \| null |
| `isVerified` | boolean |
| `averageRating` | number |
| `profileCompletionPercentage` | number (0–100) |
| `address` | string \| null |
| `user` | `UserResponse` |

**Profile completion:** 50% for non-empty `bio`, 50% for `isVerified`.

### UserResponse (nested)

| Field | Type |
| ----- | ---- |
| `id` | number |
| `email` | string |
| `mobileNumber` | string \| null |
| `name` | string |
| `isVerified` | boolean |
| `roles` | string[] |

---

## Related documentation

| Document | Description |
| -------- | ----------- |
| [AUTH_API.md](./AUTH_API.md) | Obtain Bearer token |
| [USER_API.md](./USER_API.md) | Register with `PROVIDER` role |
| [DATABASE.md](./DATABASE.md) | `providers` table |
