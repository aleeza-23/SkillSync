import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Button signupLink;

    @FXML
    private Label messageLabel;


    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Simple validation
        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Please fill in all fields");
            return;
        }

        // At this point, you can verify the credentials against your database

        System.out.println("Logged in user: " + username);

        messageLabel.setStyle("-fx-text-fill: green;");
        messageLabel.setText("Login successful!");
    }

    @FXML
    private void handleNavigateToSignup() {
        try {
            // Load signup.fxml
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("signup.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            // Get the current stage
            Stage stage = (Stage) signupLink.getScene().getWindow();

            // Set the new scene
            stage.setScene(scene);
            stage.setTitle("SkillSync - Sign Up");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}