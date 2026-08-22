# Expense Manager MT

A Spring Boot REST API for personal income, savings, and expense tracking.

Set `SUPABASE_DB_PASSWORD` before starting it. The expense-only schema can be applied directly in Supabase using [`supabase/schema.sql`](supabase/schema.sql).

For Render, define these environment variables. The application accepts either `postgresql://...` or `jdbc:postgresql://...` for `SUPABASE_DB_URL` and normalizes it automatically:

```text
SUPABASE_DB_URL=jdbc:postgresql://db.unbuqptxmnmgrqvldlea.supabase.co:5432/postgres?sslmode=require
SUPABASE_DB_USERNAME=postgres
SUPABASE_DB_PASSWORD=your-supabase-database-password
```


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
