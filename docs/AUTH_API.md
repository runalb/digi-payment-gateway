# Auth API

Base path: `/api/v1/auth`

All endpoints use `POST` and accept/return `application/json`.

## Endpoints


| #      | Method | Path                                    | Description                               | Status |
| ------ | ------ | --------------------------------------- | ----------------------------------------- | ------ |
| 1 done | `POST` | `/login`                                | Login with email and password             | 200    |
| 2 done | `POST` | `/login/email/request-otp`              | Request OTP for email login               | 200    |
| 3 done | `POST` | `/login/email/verify-otp`               | Verify email OTP and login                | 200    |
| 4 done | `POST` | `/forgot-password/email/request-otp`    | Request OTP for password reset            | 200    |
| 5 done | `POST` | `/forgot-password/email/reset-password` | Reset password with email OTP             | 204    |
| 6 done | `POST` | `/login/mobile/request-otp`             | Request OTP for mobile login              | 200    |
| 7 done | `POST` | `/login/mobile/verify-otp`              | Verify mobile OTP and login               | 200    |
| 8 done | `POST` | `/refresh-token`                        | Issue new access token from refresh token | 200    |
| 9 done | `POST` | `/logout`                               | Invalidate refresh token                  | 204    |


## Flows

### Password login

1. `POST /login`

### Email OTP login

1. `POST /login/email/request-otp`
2. `POST /login/email/verify-otp`

### Mobile OTP login

1. `POST /login/mobile/request-otp`
2. `POST /login/mobile/verify-otp`

### Forgot password (email)

1. `POST /forgot-password/email/request-otp`
2. `POST /forgot-password/email/reset-password`

### Session

- `POST /refresh-token` — obtain a new access token
- `POST /logout` — end session

---

## Request & response schemas

### `POST /login`

**Request** (`AuthLoginRequest`)


| Field      | Type   | Constraints                          |
| ---------- | ------ | ------------------------------------ |
| `email`    | string | required, valid email, max 255 chars |
| `password` | string | required, 8–128 chars                |


**Response** (`AuthLoginResponse`) — see [AuthLoginResponse](#authloginresponse)

---

### `POST /login/email/request-otp`

**Request** (`AuthEmailOtpRequest`)


| Field   | Type   | Constraints                          |
| ------- | ------ | ------------------------------------ |
| `email` | string | required, valid email, max 255 chars |


**Response** (`AuthOtpRequestResponse`) — see [AuthOtpRequestResponse](#authotprequestresponse)

---

### `POST /login/email/verify-otp`

**Request** (`AuthEmailVerifyOtpRequest`)


| Field   | Type   | Constraints                          |
| ------- | ------ | ------------------------------------ |
| `email` | string | required, valid email, max 255 chars |
| `otp`   | string | required, exactly 6 digits           |


**Response** (`AuthLoginResponse`) — see [AuthLoginResponse](#authloginresponse)

---

### `POST /forgot-password/email/request-otp`

**Request** (`AuthEmailOtpRequest`)


| Field   | Type   | Constraints                          |
| ------- | ------ | ------------------------------------ |
| `email` | string | required, valid email, max 255 chars |


**Response** (`AuthOtpRequestResponse`) — see [AuthOtpRequestResponse](#authotprequestresponse)

---

### `POST /forgot-password/email/reset-password`

**Request** (`AuthForgotPasswordResetRequest`)


| Field         | Type   | Constraints                          |
| ------------- | ------ | ------------------------------------ |
| `email`       | string | required, valid email, max 255 chars |
| `otp`         | string | required, exactly 6 digits           |
| `newPassword` | string | required, 8–128 chars                |


**Response:** empty body (204 No Content)

---

### `POST /login/mobile/request-otp`

**Request** (`AuthMobileOtpRequest`)


| Field          | Type   | Constraints                                  |
| -------------- | ------ | -------------------------------------------- |
| `mobileNumber` | string | required, E.164 format (e.g. `+14155552671`) |


**Response** (`AuthOtpRequestResponse`) — see [AuthOtpRequestResponse](#authotprequestresponse)

---

### `POST /login/mobile/verify-otp`

**Request** (`AuthMobileVerifyOtpRequest`)


| Field          | Type   | Constraints                                  |
| -------------- | ------ | -------------------------------------------- |
| `mobileNumber` | string | required, E.164 format (e.g. `+14155552671`) |
| `otp`          | string | required, exactly 6 digits                   |


**Response** (`AuthLoginResponse`) — see [AuthLoginResponse](#authloginresponse)

---

### `POST /refresh-token`

**Request** (`AuthRefreshRequest`)


| Field          | Type   | Constraints |
| -------------- | ------ | ----------- |
| `refreshToken` | string | required    |


**Response** (`AuthLoginResponse`) — see [AuthLoginResponse](#authloginresponse)

---

### `POST /logout`

**Request** (`AuthLogoutRequest`)


| Field          | Type   | Constraints |
| -------------- | ------ | ----------- |
| `refreshToken` | string | required    |


**Response:** empty body (204 No Content)

---

## Shared response types

### AuthLoginResponse


| Field                     | Type   |
| ------------------------- | ------ |
| `accessToken`             | string |
| `tokenType`               | string |
| `expiresInSeconds`        | number |
| `refreshToken`            | string |
| `refreshExpiresInSeconds` | number |


### AuthOtpRequestResponse


| Field     | Type   |
| --------- | ------ |
| `message` | string |


