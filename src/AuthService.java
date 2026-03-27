import java.sql.SQLException;

public class AuthService {

    private final UserRepository userRepository = new UserRepository();

    public SignupResult signup(String username, String email, String password, String role) {
        String passwordHash = PasswordHasher.hash(password);

        try {
            userRepository.createUser(username, email, passwordHash,role);
            return SignupResult.success("Signup successful! You can log in now.");
        } catch (SQLException e) {
            if (e.getErrorCode() == 2627 || e.getErrorCode() == 2601) {
                return SignupResult.failure("Username or email already exists");
            }
            return SignupResult.failure("Database error while creating account");
        }
    }

    public boolean login(String identifier, String password) {
        try {
            String storedHash = userRepository.getPasswordHashByIdentifier(identifier);
            if (storedHash == null) {
                return false;
            }
            return PasswordHasher.verify(password, storedHash);
        } catch (SQLException e) {
            return false;
        }
    }

    public static class SignupResult {
        private final boolean success;
        private final String message;

        private SignupResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public static SignupResult success(String message) {
            return new SignupResult(true, message);
        }

        public static SignupResult failure(String message) {
            return new SignupResult(false, message);
        }
    }
}

