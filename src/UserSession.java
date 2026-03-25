public class UserSession {
    private static int currentUserId = -1;
    private static User currentUser = null;

    private UserSession() {
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
        currentUserId = user != null ? user.getId() : -1;
    }

    public static void setCurrentUserId(int userId) {
        currentUserId = userId;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static int getCurrentUserId() {
        return currentUserId;
    }

    public static boolean isLoggedIn() {
        return currentUserId != -1;
    }

    public static void logout() {
        currentUserId = -1;
        currentUser = null;
    }
}
