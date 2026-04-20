public class PasswordHasherTest {

    public static void main(String[] args) {
        testHashFormatAndVerification();
        testWrongPasswordRejected();
        testNullAndInvalidInputs();
        System.out.println("PasswordHasherTest passed.");
    }

    private static void testHashFormatAndVerification() {
        String password = "SecurePass123!";
        String hash = PasswordHasher.hash(password);

        assertTrue(hash.startsWith("pbkdf2_sha256$"), "Hash prefix should match expected algorithm marker");
        assertTrue(PasswordHasher.verify(password, hash), "Correct password must validate");
    }

    private static void testWrongPasswordRejected() {
        String hash = PasswordHasher.hash("Correct#Password1");
        assertFalse(PasswordHasher.verify("Wrong#Password1", hash), "Wrong password must not validate");
    }

    private static void testNullAndInvalidInputs() {
        String validHash = PasswordHasher.hash("abc123");
        assertFalse(PasswordHasher.verify(null, validHash), "Null password must return false");
        assertFalse(PasswordHasher.verify("abc123", null), "Null stored hash must return false");
        assertFalse(PasswordHasher.verify("abc123", "invalid-hash-format"), "Invalid hash format must return false");
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }

    private static void assertFalse(boolean condition, String message) {
        assertTrue(!condition, message);
    }
}
