import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class DatabaseManager {

    private static final String CREATE_USERS_TABLE_SQL =
            "IF OBJECT_ID('dbo.users', 'U') IS NULL "
                    + "BEGIN "
                    + "CREATE TABLE dbo.users ("
                    + "id INT IDENTITY(1,1) PRIMARY KEY,"
                    + "username NVARCHAR(50) NOT NULL UNIQUE,"
                    + "email NVARCHAR(255) NOT NULL UNIQUE,"
                    + "password_hash NVARCHAR(512) NOT NULL,"
                    + "created_at DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME()"
                    + ");"
                    + "END";

    private DatabaseManager() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DatabaseConfig.getJdbcUrl());
    }

    public static void initializeSchema() throws SQLException {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(CREATE_USERS_TABLE_SQL);
        }
    }
}

