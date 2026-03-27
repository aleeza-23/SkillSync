import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRepository {

    public void createUser(String username, String email, String passwordHash,String role) throws SQLException {
        String sql = "INSERT INTO dbo.users (username, email, password_hash,role) VALUES (?, ?, ?,?)";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            statement.setString(2, email);
            statement.setString(3, passwordHash);
            statement.setString(4, role);
            statement.executeUpdate();
        }
    }

    public String getPasswordHashByIdentifier(String identifier) throws SQLException {
        String sql = "SELECT password_hash FROM dbo.users WHERE username = ? OR email = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, identifier);
            statement.setString(2, identifier);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("password_hash");
                }
                return null;
            }
        }
    }

    public int getUserIdByIdentifier(String identifier) throws SQLException {
        String sql = "SELECT id FROM dbo.users WHERE username = ? OR email = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, identifier);
            statement.setString(2, identifier);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("id");
                }
                return -1;
            }
        }
    }

    public User getUserById(int userId) throws SQLException {
        String sql = "SELECT id, username, email, password_hash, skills, experience, profile_picture,role FROM dbo.users WHERE id = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToUser(resultSet);
                }
                return null;
            }
        }
    }

    public void updateUserProfile(int userId, String skills, String experience, byte[] profilePicture) throws SQLException {
        String sql = "UPDATE dbo.users SET skills = ?, experience = ?, profile_picture = ? WHERE id = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, skills);
            statement.setString(2, experience);
            if (profilePicture != null) {
                statement.setBytes(3, profilePicture);
            } else {
                statement.setNull(3, java.sql.Types.VARBINARY);
            }
            statement.setInt(4, userId);
            statement.executeUpdate();
        }
    }

    private User mapResultSetToUser(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setId(resultSet.getInt("id"));
        user.setUsername(resultSet.getString("username"));
        user.setEmail(resultSet.getString("email"));
        user.setPasswordHash(resultSet.getString("password_hash"));
        user.setSkills(resultSet.getString("skills"));
        user.setExperience(resultSet.getString("experience"));
        user.setProfilePicture(resultSet.getBytes("profile_picture"));
        user.setRole(resultSet.getString("role"));
        return user;
    }
}

