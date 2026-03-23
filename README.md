# SkillSync Auth + Database Setup

This project now stores signup accounts in Azure SQL Server and verifies login credentials against stored records.

## Schema

The table used by the app is in `db/schema.sql`:

- `users.id` (identity primary key)
- `users.username` (unique)
- `users.email` (unique)
- `users.password_hash` (PBKDF2 hash string)
- `users.created_at` (UTC timestamp)

`Main` calls `DatabaseManager.initializeSchema()` on startup, so the `users` table is created automatically when the database is empty.

## Password storage

Passwords are never stored in plain text.

- `PasswordHasher.hash(...)` creates `pbkdf2_sha256$iterations$salt$hash`
- `AuthService.signup(...)` stores only the hash
- `AuthService.login(...)` retrieves hash and validates with `PasswordHasher.verify(...)`

## Connection setup

Preferred environment variable:

- `SKILLSYNC_DB_URL` = full JDBC URL

Optional fallback variable:

- `SKILLSYNC_DB_PASSWORD` = password used in the default JDBC URL in `DatabaseConfig`

If neither is set, the app uses the current hardcoded placeholder password (`xxxxx`) and connection will fail until you update it.

## Data flow in app

1. `SignupController.handleSignup()` validates fields and calls `AuthService.signup(...)`.
2. `AuthService` hashes password and calls `UserRepository.createUser(...)`.
3. `UserRepository` inserts into `dbo.users` using prepared statements.
4. `LoginController.handleLogin()` calls `AuthService.login(...)`.
5. `UserRepository.getPasswordHashByIdentifier(...)` fetches by username or email.
6. `PasswordHasher.verify(...)` compares provided password with stored hash.

