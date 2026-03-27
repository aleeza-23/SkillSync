import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.sql.SQLException;

public class LoginController {

    private final AuthService authService = new AuthService();
    private final UserRepository userRepository = new UserRepository();

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
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        // Simple validation
        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Please fill in all fields");
            return;
        }

        boolean isValidLogin = authService.login(username, password);
        if (isValidLogin) {
            messageLabel.setStyle("-fx-text-fill: green;");
            messageLabel.setText("Login successful!");
            passwordField.clear();
            
            try {
                int userId = userRepository.getUserIdByIdentifier(username);
                User user = userRepository.getUserById(userId);
                UserSession.setCurrentUser(user);
                if (user.getRole().equalsIgnoreCase("client")) {
                    navigateToClientDashboard();
                } else if (user.getRole().equalsIgnoreCase("freelancer")) {
                    navigateToFreelancerDashboard();
                }
            } catch (SQLException e) {
                System.out.println("Error fetching user data: " + e.getMessage());
                messageLabel.setStyle("-fx-text-fill: red;");
                messageLabel.setText("Error loading user profile");
            }
            return;
        }

        messageLabel.setStyle("-fx-text-fill: red;");
        messageLabel.setText("Invalid username/email or password");
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

    private void navigateToDashboard() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("dashboard.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("SkillSync - Dashboard");
        } catch (Exception e) {
            System.out.println("Error navigating to dashboard: " + e.getMessage());
            e.printStackTrace();
        }


    }
    private void navigateToClientDashboard() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("client_dashboard.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Client Dashboard");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void navigateToFreelancerDashboard() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("freelancer_dashboard.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Freelancer Dashboard");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}