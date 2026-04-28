import java.sql.SQLException;

public class AuthServiceTest {

    public static void main(String[] args) {
        testSignupSuccess();
        testSignupDuplicateError();
        testSignupDatabaseError();
        testLoginSuccess();
        testLoginWrongPassword();
        testLoginUnknownUser();
        testLoginDatabaseError();
        System.out.println("AuthServiceTest passed.");
    }

    private static void testSignupSuccess() {
        FakeUserRepository repo = new FakeUserRepository();
        AuthService service = new AuthService(repo);

        AuthService.SignupResult result = service.signup("alice", "alice@example.com", "Pass#123", "freelancer", null);

        assertTrue(result.isSuccess(), "Signup should succeed");
        assertEquals("Signup successful! You can log in now.", result.getMessage(), "Signup success message mismatch");
        assertEquals("alice", repo.lastUsername, "Username should be passed to repository");
        assertEquals("alice@example.com", repo.lastEmail, "Email should be passed to repository");
        assertEquals("freelancer", repo.lastRole, "Role should be passed to repository");
        assertTrue(repo.lastPasswordHash != null && !repo.lastPasswordHash.equals("Pass#123"),
                "Password should be hashed before storage");
    }

    private static void testSignupDuplicateError() {
        FakeUserRepository repo = new FakeUserRepository();
        repo.createUserException = sqlException(2627, "duplicate");
        AuthService service = new AuthService(repo);

        AuthService.SignupResult result = service.signup("bob", "bob@example.com", "Pass#123", "client", null);

        assertFalse(result.isSuccess(), "Signup should fail on duplicate");
        assertEquals("Username or email already exists", result.getMessage(), "Duplicate message mismatch");
    }

    private static void testSignupDatabaseError() {
        FakeUserRepository repo = new FakeUserRepository();
        repo.createUserException = sqlException(50000, "generic db failure");
        AuthService service = new AuthService(repo);

        AuthService.SignupResult result = service.signup("charlie", "charlie@example.com", "Pass#123", "client", null);

        assertFalse(result.isSuccess(), "Signup should fail on DB error");
        assertEquals("Database error while creating account", result.getMessage(), "DB error message mismatch");
    }

    private static void testLoginSuccess() {
        FakeUserRepository repo = new FakeUserRepository();
        repo.passwordHashToReturn = PasswordHasher.hash("Correct#1");
        AuthService service = new AuthService(repo);

        assertTrue(service.login("alice", "Correct#1"), "Login should succeed with valid credentials");
    }

    private static void testLoginWrongPassword() {
        FakeUserRepository repo = new FakeUserRepository();
        repo.passwordHashToReturn = PasswordHasher.hash("Correct#1");
        AuthService service = new AuthService(repo);

        assertFalse(service.login("alice", "Wrong#1"), "Login should fail with wrong password");
    }

    private static void testLoginUnknownUser() {
        FakeUserRepository repo = new FakeUserRepository();
        repo.passwordHashToReturn = null;
        AuthService service = new AuthService(repo);

        assertFalse(service.login("unknown", "any"), "Login should fail when user is not found");
    }

    private static void testLoginDatabaseError() {
        FakeUserRepository repo = new FakeUserRepository();
        repo.getPasswordHashException = sqlException(50000, "db down");
        AuthService service = new AuthService(repo);

        assertFalse(service.login("alice", "Correct#1"), "Login should fail safely on DB exception");
    }

    private static SQLException sqlException(int code, String message) {
        return new SQLException(message, "S0001", code);
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }

    private static void assertFalse(boolean condition, String message) {
        assertTrue(!condition, message);
    }

    private static void assertEquals(String expected, String actual, String message) {
        if (expected == null ? actual != null : !expected.equals(actual)) {
            throw new IllegalStateException(message + " (expected: " + expected + ", actual: " + actual + ")");
        }
    }

    private static class FakeUserRepository extends UserRepository {
        private SQLException createUserException;
        private SQLException getPasswordHashException;
        private String passwordHashToReturn;

        private String lastUsername;
        private String lastEmail;
        private String lastPasswordHash;
        private String lastRole;
        private byte[] lastProfilePicture;

        @Override
        public void createUser(String username, String email, String passwordHash, String role, byte[] profilePicture) throws SQLException {
            if (createUserException != null) {
                throw createUserException;
            }
            this.lastUsername = username;
            this.lastEmail = email;
            this.lastPasswordHash = passwordHash;
            this.lastRole = role;
            this.lastProfilePicture = profilePicture;
        }

        @Override
        public String getPasswordHashByIdentifier(String identifier) throws SQLException {
            if (getPasswordHashException != null) {
                throw getPasswordHashException;
            }
            return passwordHashToReturn;
        }
    }
}
