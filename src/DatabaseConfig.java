public final class DatabaseConfig {

    private DatabaseConfig() {
    }

    public static String getJdbcUrl() {
        String fullUrl = System.getenv("SKILLSYNC_DB_URL");
        if (fullUrl != null && !fullUrl.trim().isEmpty()) {
            return fullUrl.trim();
        }

        String password = System.getenv("PASSWORD");

        if (password == null || password.trim().isEmpty()) {
            password = "xxxxx";
        }

        return "jdbc:sqlserver://skillsyncserver.database.windows.net:1433;"
                + "database=skillsync;"
                + "user=skillsync12@skillsyncserver;"
                + "password="+ password +";"
                + "encrypt=true;"
                + "trustServerCertificate=false;"
                + "hostNameInCertificate=*.database.windows.net;"
                + "loginTimeout=30;";
    }
}

