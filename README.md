# For Creators

Full-stack storefront for handmade arts and crafts — traditional (heritage techniques) and modern (contemporary studio) lines. Brand: **For Creators**. Currency: **INR**. Payments: **Razorpay**.

## Folder structure

```
├── backend/                 Spring Boot 3.4 API (Maven, Java 17)
│   └── src/main/java/com/forcreators/api/
│       ├── web/             REST controllers + DTOs only
│       ├── service/         business logic + transactions
│       ├── repository/      Spring Data JPA
│       ├── domain/          JPA entities
│       ├── security/        JWT
│       └── config/          security, seeder, rate limit
├── frontend/                React (Vite) + Tailwind + React Query
├── docker-compose.yml       PostgreSQL 16
└── .github/workflows/ci.yml backend tests + frontend lint/build
```

Layering is strict: controllers call services; services call repositories. API responses are records in `ApiDtos`, never JPA entities.

## Data model

| Entity | Notes |
| --- | --- |
| Product | category, technique, materials[], images[] + alts, artisan, tags[], stock, one-of-a-kind |
| Category | type `TRADITIONAL` \| `MODERN`, optional parent |
| Artisan | bio, photo, workshop, process |
| ShopOrder + OrderItem | totals computed server-side; Razorpay ids |
| UserAccount | role `BUYER` \| `ADMIN`, wishlist |
| Review | rating, comment, optional images |
| CustomOrderRequest | commission / bulk enquiry |
| JournalPost, NewsletterSubscriber, ContactMessage | content + inbox |

## Prerequisites

- Java 17+
- Node.js 20+
- Optional: Docker (PostgreSQL). Default **dev** profile uses an H2 file database so you can run without Docker.

## Run locally

### API

```bash
cd backend
./mvnw spring-boot:run
```

On Windows: `.\mvnw.cmd spring-boot:run`

API: http://localhost:8080  
H2 console (dev): http://localhost:8080/h2-console (JDBC URL `jdbc:h2:file:./data/forcreators`)

### Storefront

```bash
cd frontend
npm install
npm run dev
```

App: http://localhost:5173 (Vite proxies `/api` to port 8080)

### PostgreSQL instead of H2

```bash
docker compose up -d
```

Then run the API with `SPRING_PROFILES_ACTIVE=postgres`.

## Seeded accounts

| Role | Email | Password |
| --- | --- | --- |
| Admin | `admin@forcreators.in` | `Admin@12345` |
| Buyer | `buyer@forcreators.in` | `Buyer@12345` |

The seeder also creates 3 artisans, 6 categories, 10 products (mix of traditional and modern, including one unique moon jar), and 2 journal posts.

## Environment variables

See `backend/.env.example` and `frontend/.env.example`.

| Variable | Purpose |
| --- | --- |
| `JWT_SECRET` | HMAC secret for admin/buyer JWTs |
| `SPRING_PROFILES_ACTIVE` | `dev` (H2) or `postgres` |
| `DATABASE_URL` / `USER` / `PASSWORD` | Postgres |
| `RAZORPAY_KEY_ID` / `RAZORPAY_KEY_SECRET` | Live/test Razorpay keys |
| `CORS_ORIGINS` | Comma-separated frontend origins |
| `MAIL_HOST` / `MAIL_PORT` | SMTP for contact + custom-order alerts |
| `ADMIN_NOTIFY_EMAIL` | Inbox for those alerts |
| `UPLOAD_DIR` | Local disk for product/review images |
| `VITE_API_URL` | Leave empty in dev (proxy). In production, set to the public API origin. |

Copy env files into your process environment; Spring does not load `.env` automatically unless you add a dotenv plugin.

## Tests & lint

```bash
cd backend && ./mvnw test
cd frontend && npm run lint && npm run build
```

CI runs both on GitHub Actions.

## Deployment (suggested)

- Frontend: `npm run build` → Vercel/Netlify static host. Set `VITE_API_URL` to the API URL.
- API: Render/Railway/AWS or a VPS. Use profile `postgres`, a real `JWT_SECRET`, Razorpay **test** keys first, then live keys.
- Images: swap `StorageService` for S3-compatible storage when you outgrow local disk.
- Search: Postgres/H2 `LIKE` search is enough at this size; Algolia or Meilisearch later.

Endpoint catalogue: [docs/API.md](docs/API.md)
