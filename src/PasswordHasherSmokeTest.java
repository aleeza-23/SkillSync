public class PasswordHasherSmokeTest {
    public static void main(String[] args) {
        String password = "demo-password";
        String hash = PasswordHasher.hash(password);

        boolean valid = PasswordHasher.verify(password, hash);
        boolean invalid = PasswordHasher.verify("wrong-password", hash);

        if (!valid || invalid) {
            throw new IllegalStateException("Password hashing smoke test failed");
        }

        System.out.println("PasswordHasher smoke test passed.");
    }
}

