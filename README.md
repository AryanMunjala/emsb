# EMSB Employee Management

Spring Boot employee management API with a faculty registration page.

## Requirements

- Java 25 or later
- MySQL
- A MySQL database named `emsb_db`

## Local setup

1. Copy `.env.example` to `.env`.
2. Set `DB_PASSWORD` in `.env` to your local MySQL password.
3. Start the application:

   ```powershell
   .\mvnw.cmd spring-boot:run
   ```

4. Open `http://localhost:8080/` for the faculty registration page.

The employee API is available under `/api/employees`. New and updated passwords are stored as BCrypt hashes. The API does not currently provide sign-in or authorization, so do not expose it publicly without adding access controls.

Run tests with:

```powershell
.\mvnw.cmd test
```