import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class DatabaseManager {

    private DatabaseManager() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DatabaseConfig.getJdbcUrl());
    }

    public static void initializeSchema() throws SQLException {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            String schemaSql = readSchemaFile();
            // Split on GO (case-insensitive) and execute each batch
            String[] batches = schemaSql.split("(?i)\\bGO\\b");
            for (String batch : batches) {
                batch = batch.trim();
                if (!batch.isEmpty()) {
                    statement.execute(batch);
                }
            }
        } catch (Exception e) {
            throw new SQLException("Failed to initialize schema: " + e.getMessage(), e);
        }
    }

    private static String readSchemaFile() throws Exception {
        String filePath = "db/schema.sql";
        return new String(Files.readAllBytes(Paths.get(filePath)));
    }
}


