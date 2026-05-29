# Digi Payment Gateway — API & Postman

REST API reference and Postman import guide for the Digi Payment Gateway service.

## Files

| File | Description |
|------|-------------|
| [Digi-Payment-Gateway.postman_collection.json](./Digi-Payment-Gateway.postman_collection.json) | Postman collection (requests, tests, variables) |
| [Digi-Payment-Gateway-Local.postman_environment.json](./Digi-Payment-Gateway-Local.postman_environment.json) | Local environment (`baseUrl`, `apiKey`, etc.) |

## Quick start

1. Start PostgreSQL and apply seed data:

   ```bash
   psql -U postgres -d db_digi_payment_gateway -f scripts/sql/postgresql/merchant-setup.sql
   ```

2. Run the application (default profile `dev`, port `8080`).

3. In Postman: **Import** both JSON files above.

4. Select environment **Digi Payment Gateway — Local**.

5. Run requests in this order:
   - **Integration → Generate payment link**
   - **Webhooks → TEST channel webhook (success)**
   - **Integration → Get transaction by ID**

`Generate payment link` saves `paymentId` to collection/environment variables for follow-up requests.

## Base URL

| Environment | URL |
|-------------|-----|
| Local (dev) | `http://localhost:8080` |

No servlet context path is configured.

## Authentication

| Route prefix | Header | Notes |
|--------------|--------|-------|
| `/api/v1/integration/**` | `X-API-Key: <merchant-api-key>` | Merchant must be active in DB |
| `/api/**` (other) | `Authorization: Bearer <jwt>` | JWT filter is configured; no login/token endpoint yet |
| `/webhook/**` | None | Public |

### Dev seed API key

From `scripts/sql/postgresql/merchant-setup.sql`:

```
b4bdb71a-51c5-4565-985d-e0c13f72b970
```

## Endpoints

### Integration (X-API-Key)

#### POST `/api/v1/integration/checkout/generate`

Creates a payment and returns a channel-specific payment link.

**Status:** `201 Created`

**Request body**

| Field | Type | Required | Validation |
|-------|------|----------|------------|
| `merchantReferenceId` | string | yes | not blank |
| `amount` | number | yes | min `0.01` |
| `merchantMetadataJson` | string | no | — |
| `redirectSuccessUrl` | string | no | — |
| `redirectFailureUrl` | string | no | — |

**Example**

```json
{
  "merchantReferenceId": "order-1001",
  "amount": 99.50,
  "merchantMetadataJson": "{\"table\":\"T5\"}",
  "redirectSuccessUrl": "https://merchant.example/success",
  "redirectFailureUrl": "https://merchant.example/failure"
}
```

**Response**

| Field | Type |
|-------|------|
| `paymentId` | long |
| `checkoutUrl` | string |
| `paymentChannelTxnId` | string |
| `status` | `PaymentStatusEnum` |

---

#### GET `/api/v1/integration/transactions`

Lists all payments for the authenticated merchant.

**Status:** `200 OK`  
**Response:** array of `PaymentDetailsResponse`

---

#### GET `/api/v1/integration/transactions/{id}`

Returns one payment for the authenticated merchant.

**Status:** `200 OK`  
**Path:** `id` — payment ID  
**404:** payment not found or not owned by merchant

**`PaymentDetailsResponse` fields**

| Field | Type |
|-------|------|
| `id` | long |
| `amount` | number |
| `currency` | string |
| `status` | `PaymentStatusEnum` |
| `merchantId` | long |
| `merchantReferenceId` | string |
| `merchantMetadataJson` | string |
| `paymentChannelId` | long |
| `paymentChannelName` | `PaymentChannelNameEnum` |
| `paymentChannelTxnId` | string |
| `checkoutUrl` | string |
| `createdDateTime` | ISO-8601 datetime |
| `updatedDateTime` | ISO-8601 datetime |

---

### Webhooks (public)

#### POST `/webhook/v1/payment-channel/{channelKey}`

Receives inbound webhooks from a payment channel. Payload shape is channel-specific.

**Status:** `200 OK`  
**Path:** `channelKey` — case-insensitive channel name

**Supported `channelKey` values**

`stripe`, `razorpay`, `phonepe`, `paytm`, `google_pay`, `xplorpay`, `paymob`, `test`

**TEST channel payload (local dev)**

```json
{
  "paymentStatus": "SUCCESS",
  "paymentId": 1
}
```

| Field | Required | Notes |
|-------|----------|-------|
| `paymentStatus` | yes | `PaymentStatusEnum` name, e.g. `SUCCESS`, `FAILED` |
| `paymentId` | yes | numeric payment ID |

**Response**

| Field | Type |
|-------|------|
| `status` | `PaymentStatusEnum` |
| `paymentId` | long |
| `paymentChannelTxnId` | string |
| `merchantReferenceId` | string |

For local TEST payments, `checkoutUrl` points to `/test-checkout.html` with query params (`paymentId`, `merchantId`, `amount`, `currency`).

---

### Portal (planned)

`MerchantController` is mapped at `/api/v1/merchants` but has no handlers yet. Future portal routes under `/api/**` (excluding integration) will use Bearer JWT.

---

### Actuator (dev only)

With `dev` profile, all actuator endpoints are exposed at `/actuator/*`. They are protected by Spring Security and typically return `403` without credentials.

Example: `GET /actuator/health`

## Enums

### PaymentStatusEnum

`INITIATED`, `CHECKOUT_GENERATED`, `SUCCESS`, `FAILED`, `REFUNDED`, `VOIDED`

### PaymentChannelNameEnum

`STRIPE`, `RAZORPAY`, `PHONEPE`, `PAYTM`, `GOOGLE_PAY`, `XPLORPAY`, `PAYMOB`, `TEST`

## Error responses

| Source | HTTP | Body |
|--------|------|------|
| API key / JWT filters | 401 | `{"error":"<message>"}` |
| Global exception handler | 400 / 404 / 500 | `{ "timestamp", "status", "error", "message", "path" }` |
| Validation (`@Valid`) | 400 | Spring default validation errors |
| Unknown webhook channel | 404 | `"Unknown payment channel: {channelKey}"` |

## Collection variables

| Variable | Default (local) | Description |
|----------|-----------------|-------------|
| `baseUrl` | `http://localhost:8080` | Server base URL |
| `apiKey` | seed merchant API key | `X-API-Key` value |
| `paymentId` | `1` | Set automatically after generate link |
| `channelKey` | `test` | Webhook channel segment |
| `bearerToken` | *(empty)* | For future portal APIs |

## Collection folders

| Folder | Auth | Requests |
|--------|------|----------|
| Integration (X-API-Key) | `X-API-Key` | Generate link, list transactions, get by ID |
| Webhooks (public) | None | TEST success/failed, generic channel template |
| Portal (Bearer JWT) — planned | Bearer | Placeholder merchants API |
| Actuator (dev) | — | Health |
