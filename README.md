# Expense Manager MT

A Spring Boot REST API for personal income, savings, and expense tracking.

Set `SUPABASE_DB_PASSWORD` before starting it. The expense-only schema can be applied directly in Supabase using [`supabase/schema.sql`](supabase/schema.sql).

For Render, use the Supabase **Session pooler** connection details from `Supabase Dashboard -> Connect -> Session pooler`. The pooler is recommended because Render may not be able to reach the direct database host over IPv6. Define these environment variables:

```text
SUPABASE_DB_URL=postgresql://aws-0-ap-northeast-2.pooler.supabase.com:5432/postgres?sslmode=require
SUPABASE_DB_USERNAME=postgres.unbuqptxmnmgrqvldlea
SUPABASE_DB_PASSWORD=your-supabase-database-password
```

Copy the exact host, port, username, and database name shown by Supabase; do not type the example placeholders. The application accepts either `postgresql://...` or `jdbc:postgresql://...` and normalizes it automatically.

```powershell
mvn spring-boot:run
```

The JDBC URL is configured for the Supabase project supplied in the brief. Keep the password in an environment variable and never commit it.

## Docker

Build and run the API from the project root:

```powershell
docker build -t expense-manager-mt .
docker run --rm -p 8080:8080 -e SUPABASE_DB_PASSWORD=your-password expense-manager-mt
```

The API is available at `http://localhost:8080/api`.
