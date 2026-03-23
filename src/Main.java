import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        // Initialize database connection
        String url = "jdbc:sqlserver://skillsyncserver.database.windows.net:1433;"
                + "database=skillsync;"
                + "user=skillsync12@skillsyncserver;"
                + "password=xxxxx;"
                + "encrypt=true;"
                + "trustServerCertificate=false;"
                + "hostNameInCertificate=*.database.windows.net;"
                + "loginTimeout=30;";

        try (Connection conn = DriverManager.getConnection(url)) {

            System.out.println("✅ Connected to Azure SQL Database!");

            // Test query
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT GETDATE()");

            while (rs.next()) {
                System.out.println("Server Time: " + rs.getString(1));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Load the UI
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        stage.setTitle("SkillSync - Log In");
        stage.setScene(scene);
        stage.show();
    }
}
