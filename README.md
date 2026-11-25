# QueueLess India

A responsive React + Vite frontend and Java Spring Boot API for a two-sided virtual queue and appointment platform. Customers can discover organizations and join queues. Organization owners can manage services, staff, and queue status through the secured API.

## Run locally

```bash
npm install
npm run dev
```

Create a production build with `npm run build`; preview it with `npm run preview`.

## Deploy

Deploy the frontend to Vercel and the Spring Boot API plus PostgreSQL to Render. The included `vercel.json` routes app URLs to the frontend entry point. Vercel detects Vite automatically; use `npm run build` as the build command and `dist` as the output directory. Set the Vercel environment variable `VITE_API_BASE_URL` to your deployed API URL ending in `/api/v1`. The backend deployment steps and exact environment variables are in [backend/README.md](backend/README.md).

## Start everything locally

Use two terminal windows. The API requires a Java 21+ JDK and Maven. For the fastest start, the backend's `demo` profile uses an in-memory H2 database; Docker is only needed for persistent PostgreSQL.

**Terminal 1 — API** (from `backend`):

```powershell
mvn spring-boot:run "-Dspring-boot.run.profiles=demo"
```

**Terminal 2 — frontend** (from the project root):

```powershell
npm install
npm run dev
```

Open <http://localhost:5173>. The API explorer is at <http://localhost:8080/swagger-ui/index.html>. Create your own customer or organization account to get started.

The full API run guide, endpoints, PostgreSQL instructions, and deployment settings are in [backend/README.md](backend/README.md). PostgreSQL is the persistent database option; Flyway manages its schema. The `demo` profile uses an empty in-memory database.
