# QueueLess India API

Spring Boot 3.5, Java 21, PostgreSQL, Flyway migrations, BCrypt password hashing, JWT authentication, bean validation, role-based authorization, and OpenAPI/Swagger UI.

## Quick start (no Docker required)

Requires a Java 21 or newer JDK and Maven 3.6.3 or newer. Check with `java -version` and `mvn -version`.

From the `backend` folder, start a self-contained H2 development database and API:

```powershell
mvn spring-boot:run "-Dspring-boot.run.profiles=demo"
```

This in-memory database resets when the API stops. It is intended for learning and trying the flows. Health: <http://localhost:8080/actuator/health>. API explorer: <http://localhost:8080/swagger-ui/index.html>.

The demo profile starts with no organizations. Register a customer or organization account and add your own organization and services.

## QueueLess passes and Razorpay

New customer accounts receive 5 days of access; new organization accounts receive a 2-hour workspace trial. Customer passes cost ₹9/7 days, ₹49/30 days, or ₹99/365 days. The organization plan costs ₹1,999/30 days. Access windows, profiles, reviews and verified payments are stored in the database.

Payments use server-created Razorpay orders and server-side payment/signature checks. For test checkout, set `RAZORPAY_KEY_ID` and `RAZORPAY_KEY_SECRET` from your Razorpay test-mode dashboard in the backend process environment before starting Spring Boot. See `.env.example` for the variable names. The backend deliberately reports payments as unavailable until both keys are set. Never put the key secret in frontend variables or commit actual keys.

PowerShell example (replace the values with your Razorpay **test** keys):

```powershell
$env:RAZORPAY_KEY_ID = "rzp_test_your_key_id"
$env:RAZORPAY_KEY_SECRET = "your_test_key_secret"
mvn spring-boot:run "-Dspring-boot.run.profiles=demo"
```

## PostgreSQL local database

Install and start Docker Desktop, then use the persistent PostgreSQL setup:

```powershell
docker compose up -d
$env:SPRING_PROFILES_ACTIVE = "local"
mvn spring-boot:run
```

PostgreSQL data is stored in the `queueless-postgres-data` Docker volume. Flyway creates and upgrades the schema automatically. Stop the database with `docker compose down` (keeps data) or `docker compose down -v` (deletes the database volume).

Set `APP_SEED_ENABLED=false` to disable the PostgreSQL development seed.

## Connect the frontend

The frontend runs at <http://localhost:5173>. Configure its API URL by copying the root `.env.example` file to `.env.local` in the project root (or add this line):

```text
VITE_API_BASE_URL=http://localhost:8080/api/v1
```

Restart `npm run dev` after changing the environment file. The user-facing frontend flow can browse organizations and join the public queue; use Swagger UI to register/login accounts and exercise the owner tools. The current prototype still keeps some dashboard presentation data on the frontend.

## API map

| Method | Endpoint | Access | Purpose |
|---|---|---|---|
| POST | `/api/v1/auth/register` | Public | Create a customer account |
| POST | `/api/v1/auth/register-owner` | Public | Create an organization owner account |
| POST | `/api/v1/auth/login` | Public | Get a JWT |
| GET | `/api/v1/organizations?city=Bengaluru` | Public | Find active organizations |
| GET | `/api/v1/organizations/{id}` | Public | Organization details and live wait estimate |
| GET | `/api/v1/organizations/{id}/services` | Public | Organization services |
| GET | `/api/v1/organizations/{id}/staff` | Public | Organization staff |
| POST | `/api/v1/owner/organizations` | Owner JWT | Create an organization |
| POST | `/api/v1/owner/organizations/{id}/services` | Owner JWT | Add a service |
| POST | `/api/v1/owner/organizations/{id}/staff` | Owner JWT | Add a staff member |
| POST | `/api/v1/queues/join` | Customer JWT | Join a queue; returns a private tracking code |
| POST | `/api/v1/queues/me/join` | Customer JWT | Join a queue linked securely to the signed-in customer |
| GET | `/api/v1/queues/track/{code}?email=...` | Code + email | Check a queue entry |
| DELETE | `/api/v1/queues/track/{code}?email=...` | Code + email | Cancel a waiting entry |
| GET | `/api/v1/queues/me` | Customer JWT | List the signed-in customer's active queues |
| GET | `/api/v1/queues/me/history` | Customer JWT | List completed, cancelled, and no-show appointments |
| POST | `/api/v1/queues/me/{entryId}/cancel` | Customer JWT | Cancel an appointment with a required reason |
| GET/POST | `/api/v1/queues/me/{entryId}/messages` | Customer JWT | Read or send messages for the customer's appointment |
| GET | `/api/v1/owner/organizations/{id}/queue?date=YYYY-MM-DD` | Owner JWT | Inspect the daily queue |
| GET | `/api/v1/owner/organizations/{id}/history` | Owner JWT | List completed, cancelled, and no-show appointments |
| GET/POST | `/api/v1/owner/organizations/{id}/queue/{entryId}/messages` | Owner JWT | Read or reply to appointment messages |
| POST | `/api/v1/owner/organizations/{id}/queue/{entryId}/call` | Owner JWT | Start serving a customer |
| POST | `/api/v1/owner/organizations/{id}/queue/{entryId}/complete` | Owner JWT | Complete the service |
| POST | `/api/v1/owner/organizations/{id}/queue/{entryId}/no-show` | Owner JWT | Mark a waiting customer as a no-show |
| PUT | `/api/v1/owner/organizations/{id}/queue/{entryId}/staff` | Owner JWT | Assign a staff member |

Use the Swagger **Authorize** button with `Bearer <accessToken>` for protected endpoints. Public queue joining accepts a visitor name and email; a registered customer with the same email is linked automatically. Keep the tracking code private.

## Try a complete flow

Open Swagger UI. Use `POST /api/v1/auth/login` with the seeded owner credentials. Click **Authorize** and paste the returned token (include `Bearer `). Create an organization with `POST /api/v1/owner/organizations`, add at least one service with `POST /api/v1/owner/organizations/{id}/services`, and add staff with the matching `/staff` endpoint. From the frontend, open that organization and join its queue. In Swagger, open its owner queue, call the next customer, then complete service. The visitor can track their status from the confirmation tracking code and email.

Quick API fetch from PowerShell:

```powershell
Invoke-RestMethod http://localhost:8080/api/v1/organizations
```

Join a queue directly from PowerShell (replace the service ID with one returned by the service list):

```powershell
$org = (Invoke-RestMethod http://localhost:8080/api/v1/organizations)[0]
$service = (Invoke-RestMethod "http://localhost:8080/api/v1/organizations/$($org.id)/services")[0]
$entry = Invoke-RestMethod -Method Post http://localhost:8080/api/v1/queues/join `
  -ContentType 'application/json' `
  -Body (@{ serviceId=$service.id; customerName='Aarav Kapoor'; customerEmail='aarav@example.com' } | ConvertTo-Json)
$entry
Invoke-RestMethod "http://localhost:8080/api/v1/queues/track/$($entry.trackingCode)?email=aarav%40example.com"
```

## Production configuration

Set `DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD`, `JWT_SECRET` (at least 32 random bytes), and `CORS_ALLOWED_ORIGINS` in the deployment environment. Keep `APP_SEED_ENABLED=false` outside local development, use managed PostgreSQL with TLS/backups, and terminate HTTPS at the platform. Do not use the example credentials or JWT secret in a live deployment.

## Deploy the full project (Vercel + Render)

Deploy the frontend and backend as separate services. The frontend is static Vite output; the backend requires a persistent PostgreSQL database. Do not deploy the `demo` profile, because it uses an in-memory database that resets when the process restarts.

### 1. Deploy the backend and database on Render

1. Push this project to a private or public GitHub repository.
2. In Render, create a managed PostgreSQL database and a **Web Service** from that repository. Choose **Docker** as the runtime.
3. Set the Dockerfile path to `./backend/Dockerfile` and the Docker build context to `./backend`.
4. Add these service environment variables in Render:

   | Variable | Value |
   | --- | --- |
   | `DATABASE_URL` | `jdbc:postgresql://<internal-db-host>:5432/<database-name>` |
   | `DATABASE_USERNAME` | Database user shown by Render |
   | `DATABASE_PASSWORD` | Database password shown by Render |
   | `JWT_SECRET` | A unique random secret with at least 32 bytes |
   | `CORS_ALLOWED_ORIGINS` | Your Vercel site origin, for example `https://queueless-india.vercel.app` |
   | `APP_SEED_ENABLED` | `false` |

   Use the database's **internal host** because both services are on Render. Enter the host, database name, username, and password separately as shown above; `DATABASE_URL` must begin with `jdbc:postgresql://`. Leave the Spring profile unset so Flyway initializes PostgreSQL. Render supplies `PORT` automatically. Add Razorpay `RAZORPAY_KEY_ID` and `RAZORPAY_KEY_SECRET` only when you have the production keys; keep the secret only in Render.

5. After deploy, check `https://<your-api-host>/actuator/health`. The response should include `"status":"UP"`.

### 2. Deploy the frontend on Vercel

1. Import the same repository into Vercel. Keep the project root at the repository root.
2. Use framework **Vite**, build command `npm run build`, and output directory `dist` (normally auto-detected).
3. Add `VITE_API_BASE_URL` with the deployed API URL plus `/api/v1`, for example `https://queueless-api.onrender.com/api/v1`.
4. Deploy. Copy the resulting site origin, such as `https://queueless-india.vercel.app`, into the backend's `CORS_ALLOWED_ORIGINS` variable and redeploy the backend.
5. Open the Vercel URL, register a customer account, then register an organization account and add its services. The demo database is intentionally empty.

`VITE_API_BASE_URL` is embedded into the frontend at build time. If you change it in Vercel, redeploy the frontend. If you change backend environment variables, redeploy the backend. Never add `JWT_SECRET`, database credentials, or Razorpay secret values to Vercel's `VITE_` variables.
# Master administrator

The master administrator is created by the backend at startup when these environment variables are configured:

- `MASTER_ADMIN_EMAIL` (defaults to `masteradmin@gmail.com`)
- `MASTER_ADMIN_PASSWORD` (required; set this to the password you want)

Set them in Render under **queue-less-api → Environment → Environment Variables**, then redeploy. Never commit the password to GitHub. For local PowerShell, set `$env:MASTER_ADMIN_PASSWORD = 'masteradmin@'` before running Maven. The account signs in through the normal **Sign in** form. The server grants the master-admin role only to the seeded account; public registration cannot create one.

The admin workspace at `/api/v1/admin` provides global counts, customer and organization lists, individual appointment history, and account removal. Removal deactivates the record and cancels its active appointments while retaining historical records for reporting. Admin endpoints require a valid JWT with the `MASTER_ADMIN` role.
