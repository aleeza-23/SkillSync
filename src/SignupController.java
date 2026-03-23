

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class SignupController {


    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;


    @FXML
    private Button signupButton;


    @FXML
    private Label messageLabel;


    @FXML
    private void handleSignup() {
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String confirm = confirmPasswordField.getText();

        // Simple validation
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            messageLabel.setText("Please fill in all fields");
            return;
        }

        if (!password.equals(confirm)) {
            messageLabel.setText("Passwords do not match");
            return;
        }

        // At this point, you can save the data to your database or array

        System.out.println("Signed up user: " + username + ", " + email);

        messageLabel.setText("Signup successful!");
    }
}