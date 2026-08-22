# Expense Manager MT

A Spring Boot REST API for personal income, savings, and expense tracking.

Set `SUPABASE_DB_PASSWORD` before starting it. The expense-only schema can be applied directly in Supabase using [`supabase/schema.sql`](supabase/schema.sql).

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
