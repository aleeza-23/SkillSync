import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.ComboBox;

import java.io.File;
import java.nio.file.Files;

public class SignupController {

    private final AuthService authService = new AuthService();


    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private ComboBox<String> roleComboBox;


    @FXML
    private Button signupButton;

    @FXML
    private Button loginLink;

    @FXML
    private Label messageLabel;
    @FXML
    private ImageView profileImageView;
    @FXML
    private Label pictureStatusLabel;
    @FXML
    private Button uploadPictureButton;
    private byte[] profilePicture;
    @FXML
    public void initialize() {
        roleComboBox.getItems().addAll("freelancer", "client");
        AvatarUtils.applyAvatar(profileImageView, null);
        pictureStatusLabel.setText("No picture selected");
    }

    @FXML
    private void handleSignup() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirm = confirmPasswordField.getText();
        String role = roleComboBox.getValue();

        // Simple validation
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirm.isEmpty()|| role == null) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Please fill in all fields");
            return;
        }

        if (!password.equals(confirm)) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Passwords do not match");
            return;
        }

        AuthService.SignupResult result = authService.signup(username, email, password, role, profilePicture);
        if (result.isSuccess()) {
            messageLabel.setStyle("-fx-text-fill: green;");
            messageLabel.setText(result.getMessage());
            clearSignupForm();
            return;
        }

        messageLabel.setStyle("-fx-text-fill: red;");
        messageLabel.setText(result.getMessage());
    }

    private void clearSignupForm() {
        usernameField.clear();
        emailField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        roleComboBox.getSelectionModel().clearSelection();
        profilePicture = null;
        AvatarUtils.applyAvatar(profileImageView, null);
        pictureStatusLabel.setText("No picture selected");
    }

    @FXML
    private void handleUploadPicture() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Profile Picture");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.jpeg", "*.png"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        Stage stage = (Stage) uploadPictureButton.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);
        if (file == null) {
            return;
        }

        try {
            profilePicture = Files.readAllBytes(file.toPath());
            AvatarUtils.applyAvatar(profileImageView, profilePicture);
            pictureStatusLabel.setText("Selected: " + file.getName());
        } catch (Exception e) {
            profilePicture = null;
            AvatarUtils.applyAvatar(profileImageView, null);
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Unable to load selected image");
            pictureStatusLabel.setText("No picture selected");
        }
    }

    @FXML
    private void handleNavigateToLogin() {
        try {
            UserSession.logout();
            // Load login.fxml
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            // Get the current stage
            Stage stage = (Stage) loginLink.getScene().getWindow();

            // Set the new scene
            stage.setScene(scene);
            stage.setTitle("SkillSync - Log In");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}