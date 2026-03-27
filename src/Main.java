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
        try {
            DatabaseManager.initializeSchema();
            System.out.println("Connected to Azure SQL Database and schema is ready.");
        } catch (Exception e) {
            System.out.println("Database initialization failed: " + e.getMessage());
        }

        // Load the UI
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        stage.setTitle("SkillSync - Log In");
        stage.setScene(scene);
        stage.show();
    }
}
