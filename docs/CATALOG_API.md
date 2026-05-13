# Catalog API

Base path: `/api/v1/catalog`

All endpoints accept/return `application/json` unless noted.

## Authentication

| Operation | Auth |
| --------- | ---- |
| `GET` (categories, services) | Bearer JWT — any authenticated user |
| `POST`, `PATCH`, `DELETE` | Bearer JWT — `SUPER_ADMIN` role required |

Header: `Authorization: Bearer <accessToken>`

---

## Endpoints

### Categories

| # | Method | Path | Description | Status |
| - | ------ | ---- | ----------- | ------ |
| 1 | `GET` | `/categories` | List categories | 200 |
| 2 | `GET` | `/categories/{categoryId}` | Get category | 200 |
| 3 | `POST` | `/categories` | Create category | 201 |
| 4 | `PATCH` | `/categories/{categoryId}` | Update category | 200 |
| 5 | `DELETE` | `/categories/{categoryId}` | Delete category | 204 |

### Services

| # | Method | Path | Description | Status |
| - | ------ | ---- | ----------- | ------ |
| 6 | `GET` | `/categories/{categoryId}/services` | List services in category | 200 |
| 7 | `POST` | `/categories/{categoryId}/services` | Create service in category | 201 |
| 8 | `GET` | `/services` | List all services | 200 |
| 9 | `GET` | `/services/{serviceId}` | Get service | 200 |
| 10 | `PATCH` | `/services/{serviceId}` | Update service | 200 |
| 11 | `DELETE` | `/services/{serviceId}` | Delete service | 204 |

---

## Request & response schemas

### `GET /categories` · `GET /categories/{categoryId}`

**Response** (`CatalogCategoryResponse`) — see [CatalogCategoryResponse](#catalogcategoryresponse)

List returns `CatalogCategoryResponse[]`.

---

### `POST /categories`

**Request** (`CatalogCategoryCreateRequest`)

| Field | Type | Constraints |
| ----- | ---- | ----------- |
| `name` | string | required, max 255 chars |
| `description` | string | max 2000 chars |
| `displayOrder` | number | optional |
| `active` | boolean | optional |

**Response** (`CatalogCategoryResponse`)

---

### `PATCH /categories/{categoryId}`

**Request** (`CatalogCategoryUpdateRequest`) — all fields optional.

| Field | Type | Constraints |
| ----- | ---- | ----------- |
| `name` | string | max 255 chars |
| `description` | string | max 2000 chars |
| `displayOrder` | number | |
| `active` | boolean | |

**Response** (`CatalogCategoryResponse`)

---

### `DELETE /categories/{categoryId}`

**Response:** empty body (204 No Content)

---

### `GET /categories/{categoryId}/services`

**Response:** `CatalogServiceResponse[]`

---

### `POST /categories/{categoryId}/services`

**Request** (`CatalogServiceCreateRequest`)

| Field | Type | Constraints |
| ----- | ---- | ----------- |
| `name` | string | required, max 512 chars |
| `description` | string | max 4000 chars |
| `displayOrder` | number | optional |
| `active` | boolean | optional |

**Response** (`CatalogServiceResponse`) — see [CatalogServiceResponse](#catalogserviceresponse)

---

### `GET /services` · `GET /services/{serviceId}`

**Response** (`CatalogServiceResponse`)

List returns `CatalogServiceResponse[]`.

---

### `PATCH /services/{serviceId}`

**Request** (`CatalogServiceUpdateRequest`) — all fields optional.

| Field | Type | Constraints |
| ----- | ---- | ----------- |
| `name` | string | max 512 chars |
| `description` | string | max 4000 chars |
| `displayOrder` | number | |
| `active` | boolean | |
| `categoryId` | number | move service to another category |

**Response** (`CatalogServiceResponse`)

---

### `DELETE /services/{serviceId}`

**Response:** empty body (204 No Content)

---

## Shared response types

### CatalogCategoryResponse

| Field | Type |
| ----- | ---- |
| `id` | number |
| `name` | string |
| `description` | string \| null |
| `displayOrder` | number |
| `active` | boolean |
| `createdDateTime` | string (ISO-8601) |
| `updatedDateTime` | string (ISO-8601) |

### CatalogServiceResponse

| Field | Type |
| ----- | ---- |
| `id` | number |
| `name` | string |
| `description` | string \| null |
| `displayOrder` | number |
| `active` | boolean |
| `category` | `CatalogCategoryResponse` |
| `createdDateTime` | string (ISO-8601) |
| `updatedDateTime` | string (ISO-8601) |

---

## Related documentation

| Document | Description |
| -------- | ----------- |
| [AUTH_API.md](./AUTH_API.md) | Obtain Bearer token |
| [USER_API.md](./USER_API.md) | Register user with `SUPER_ADMIN` role |
| [DATABASE.md](./DATABASE.md) | `catalog_category`, `catalog_service` tables |
