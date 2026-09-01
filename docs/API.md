# For Creators API

Base URL (local): `http://localhost:8080`

Auth: `Authorization: Bearer <jwt>` after `POST /api/auth/login` or `/register`.

## Public

| Method | Path | Notes |
| --- | --- | --- |
| POST | `/api/auth/register` | `{ email, password, name }` |
| POST | `/api/auth/login` | `{ email, password }` |
| GET | `/api/products` | Query: `categoryId`, `type` (`TRADITIONAL`\|`MODERN`), `material`, `minPrice`, `maxPrice`, `technique`, `artisanId`, `q`, `sort` (`newest`\|`price-asc`\|`price-desc`\|`name`), `page`, `size` |
| GET | `/api/products/featured` | |
| GET | `/api/products/{id}` | |
| GET | `/api/products/{id}/related` | |
| GET | `/api/categories` | |
| GET | `/api/artisans` | |
| GET | `/api/artisans/{id}` | |
| GET | `/api/reviews/product/{productId}` | |
| GET | `/api/journal` | |
| GET | `/api/journal/{slug}` | |
| POST | `/api/checkout` | Guest: include `guestEmail`, `guestName`. Body: `{ items: [{ productId, quantity }], shippingAddress }` |
| POST | `/api/payments/verify` | `{ razorpayOrderId, razorpayPaymentId, razorpaySignature }` |
| POST | `/api/contact` | Rate limited |
| POST | `/api/custom-orders` | Rate limited |
| POST | `/api/newsletter` | Rate limited |
| GET | `/api/files/{filename}` | Uploaded images |
| GET | `/sitemap.xml` | |
| GET | `/robots.txt` | |

## Authenticated buyer

| Method | Path |
| --- | --- |
| GET | `/api/auth/me` |
| GET | `/api/orders` |
| GET | `/api/wishlist` |
| POST | `/api/wishlist/{productId}` |
| POST | `/api/reviews/product/{productId}` |
| POST | `/api/uploads` | multipart `file` |

## Admin (`ROLE_ADMIN`)

| Method | Path |
| --- | --- |
| POST/PUT/DELETE | `/api/admin/products` / `{id}` |
| POST/PUT/DELETE | `/api/admin/categories` / `{id}` |
| POST/PUT/DELETE | `/api/admin/artisans` / `{id}` |
| GET | `/api/admin/orders` |
| PATCH | `/api/admin/orders/{id}` `{ "status": "SHIPPED" }` |
| GET | `/api/admin/custom-orders` |
| PATCH | `/api/admin/custom-orders/{id}` `{ "status": "REVIEWING" }` |
| POST/DELETE | `/api/admin/journal` / `{id}` |
| POST | `/api/admin/uploads` |

## Checkout example (demo mode, no Razorpay keys)

```http
POST /api/checkout
Content-Type: application/json

{
  "items": [{ "productId": 1, "quantity": 1 }],
  "shippingAddress": "12 Loom Lane, Bengaluru 560001",
  "guestEmail": "guest@example.com",
  "guestName": "Guest"
}
```

Then `POST /api/payments/verify` with the returned `razorpayOrderId` (prefix `order_demo_` when keys are unset). Inventory decrements only after verify.

Import these rows into Postman as a collection or replay with curl.
