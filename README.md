# Week 7: E-commerce Backend (Spring Data JPA + PostgreSQL)

A clean Week 7 implementation of an e-commerce backend with Spring Boot, Spring Data JPA, Flyway migrations, transactional order processing, and optimized repository queries.

## Tech Stack
- Java 17
- Spring Boot 3.x
- Spring Data JPA / Hibernate
- PostgreSQL 15 (profile: `postgres`)
- H2 (default local profile)
- Flyway
- HikariCP
- Lombok

## Implemented Modules
- Product catalog with category relationship and inventory management
- Order processing with stock deduction inside `@Transactional`
- Payment processing with order status updates
- User registration/login/profile endpoints
- JPA auditing with `@CreatedDate` and `@LastModifiedDate`
- Custom repository queries (`@Query`, native report query, fetch-join)
- Indexes and constraints via Flyway migrations

## Database Migrations
Located at `src/main/resources/db/migration`:
- `V1__initial_schema.sql`
- `V2__seed_data.sql`
- `V3__add_indexes.sql`

## API Endpoints

### Products
- `GET /api/products?name=&categoryId=&page=0&size=10`
- `GET /api/products/{id}`
- `POST /api/products`
- `PUT /api/products/{id}`
- `DELETE /api/products/{id}` (soft delete via `isActive=false`)

### Orders
- `GET /api/orders?userId=1&page=0&size=10`
- `GET /api/orders/{id}`
- `POST /api/orders`
- `PUT /api/orders/{id}/cancel`
- `GET /api/orders/report/daily?startDate=2026-02-01T00:00:00`

### Payments
- `POST /api/payments`
- `GET /api/payments/{id}`

### Authentication / Users
- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/users/profile?id=1`
- `PUT /api/users/profile?id=1`

## Running

### Default (H2 local)
```bash
mvn spring-boot:run
```

### PostgreSQL + Flyway
1. Start PostgreSQL and create DB `ecommerce_db`
2. Run with profile:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=postgres
```

### Windows PowerShell helper
Use the included script to avoid command parsing issues and to auto-fallback when PostgreSQL is unavailable:
```powershell
.\start-backend.ps1            # starts with H2
.\start-backend.ps1 -Postgres  # tries Docker/local PostgreSQL, then runs postgres profile
```

Optional PostgreSQL env overrides:
```powershell
$env:DB_URL="jdbc:postgresql://localhost:5432/ecommerce_db"
$env:DB_USER="postgres"
$env:DB_PASSWORD="password"
.\start-backend.ps1 -Postgres
```

## Build & Test
```bash
mvn test
mvn -DskipTests package
```

## Notes
- Password handling is plain text for assignment simplicity; use hashing (BCrypt) in production.
- Role-based authorization can be added on top of existing auth endpoints.
