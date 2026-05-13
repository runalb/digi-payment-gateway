# User API

Base path: `/api/v1/users`

All endpoints accept/return `application/json` unless noted.

## Authentication


| Endpoint                    | Auth                                                |
| --------------------------- | --------------------------------------------------- |
| `POST /`                    | Public (registration)                               |
| `GET /{userId}`             | Bearer JWT — authenticated user must match `userId` |
| `PATCH /{userId}`           | Bearer JWT — owner only                             |
| `DELETE /{userId}`          | Bearer JWT — owner only                             |
| `POST /{userId}/reactivate` | Bearer JWT — owner only                             |


Header for protected routes: `Authorization: Bearer <accessToken>`

## Endpoints


| #      | Method   | Path                   | Description                       | Status |
| ------ | -------- | ---------------------- | --------------------------------- | ------ |
| 1 done | `POST`   | `/`                    | Register a new user               | 201    |
| 2 done | `GET`    | `/{userId}`            | Get own profile                   | 200    |
| 3 done | `PATCH`  | `/{userId}`            | Partial profile update            | 200    |
| 4 done | `DELETE` | `/{userId}`            | Soft-delete (deactivate) account  | 204    |
| 5 done | `POST`   | `/{userId}/reactivate` | Reactivate a soft-deleted account | 200    |


> `GET /` (list users) is not implemented — commented out in `UserController`.

---

## Request & response schemas

### `POST /`

**Request** (`UserCreateRequest`)


| Field          | Type     | Constraints                                                       |
| -------------- | -------- | ----------------------------------------------------------------- |
| `email`        | string   | required, valid email, max 255 chars                              |
| `password`     | string   | required, 8–128 chars                                             |
| `name`         | string   | required, max 255 chars                                           |
| `mobileNumber` | string   | optional, E.164 format (e.g. `+14155552671`)                      |
| `roles`        | string[] | required, non-empty; each value must exist in DB (`RoleNameEnum`) |


**Role values:** `CUSTOMER`, `PROVIDER`, `ADMIN`, `SUPER_ADMIN` — seed with `scripts/seed-roles.sql`.

**Response** (`UserResponse`) — see [UserResponse](#userresponse)

**Errors**


| Status | When                                      |
| ------ | ----------------------------------------- |
| 400    | Unknown role in `roles`                   |
| 409    | Email or mobile number already registered |


---

### `GET /{userId}`

**Path parameter:** `userId` — must equal the authenticated user's id.

**Response** (`UserResponse`) — see [UserResponse](#userresponse)

**Errors**


| Status | When                                       |
| ------ | ------------------------------------------ |
| 401    | Missing/invalid JWT                        |
| 403    | `userId` does not match authenticated user |
| 404    | User not found                             |


---

### `PATCH /{userId}`

**Path parameter:** `userId` — owner only.

**Request** (`UserUpdateRequest`) — at least one field required; all fields optional.


| Field          | Type    | Constraints                                                                                |
| -------------- | ------- | ------------------------------------------------------------------------------------------ |
| `email`        | string  | valid email, max 255 chars                                                                 |
| `name`         | string  | max 255 chars                                                                              |
| `mobileNumber` | string  | E.164 format (e.g. `+14155552671`)                                                         |
| `password`     | string  | 8–128 chars (present in DTO but **not applied** by `updateUser`; use forgot-password flow) |
| `isVerified`   | boolean |                                                                                            |


**Response** (`UserResponse`) — see [UserResponse](#userresponse)

**Errors**


| Status | When                                          |
| ------ | --------------------------------------------- |
| 400    | Empty request body                            |
| 403    | Not the account owner                         |
| 409    | Email or mobile already taken by another user |


---

### `DELETE /{userId}`

**Path parameter:** `userId` — owner only.

Sets `isDeleted = true` on the user (soft delete).

**Response:** empty body (204 No Content)

**Errors**


| Status | When                  |
| ------ | --------------------- |
| 403    | Not the account owner |
| 404    | User not found        |


---

### `POST /{userId}/reactivate`

**Path parameter:** `userId` — owner only.

Sets `isDeleted = false`.

**Response** (`UserResponse`) — see [UserResponse](#userresponse)

**Errors**


| Status | When                  |
| ------ | --------------------- |
| 403    | Not the account owner |
| 404    | User not found        |


---

## Shared response type

### UserResponse


| Field          | Type          |
| -------------- | ------------- |
| `id`           | number        |
| `email`        | string        |
| `mobileNumber` | string | null |
| `name`         | string        |
| `isVerified`   | boolean       |
| `roles`        | string[]      |


`roles` contains sorted, distinct `RoleNameEnum` names (e.g. `["CUSTOMER"]`).

---

## Related documentation


| Document                             | Description                  |
| ------------------------------------ | ---------------------------- |
| [AUTH_API.md](./AUTH_API.md)         | Login, OTP, refresh, logout  |
| [ARCHITECTURE.md](./ARCHITECTURE.md) | Security and ownership rules |
| [DATABASE.md](./DATABASE.md)         | `users` table schema         |


