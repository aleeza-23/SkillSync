import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.sql.SQLException;

public class ProfileController {

    private final UserRepository userRepository = new UserRepository();
    private byte[] currentProfilePicture;
    private File selectedImageFile;

    @FXML
    private Label usernameLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private TextArea skillsTextArea;

    @FXML
    private TextArea experienceTextArea;

    @FXML
    private ImageView profileImageView;

    @FXML
    private Button uploadPictureButton;

    @FXML
    private Button saveButton;

    @FXML
    private Button backButton;

    @FXML
    private Label messageLabel;

    @FXML
    private Label pictureStatusLabel;

    @FXML
    public void initialize() {
        User user = UserSession.getCurrentUser();
        if (user != null) {
            usernameLabel.setText("Username: " + user.getUsername());
            emailLabel.setText("Email: " + user.getEmail());
            skillsTextArea.setText(user.getSkills() != null ? user.getSkills() : "");
            experienceTextArea.setText(user.getExperience() != null ? user.getExperience() : "");

            currentProfilePicture = user.getProfilePicture();
            if (currentProfilePicture != null && currentProfilePicture.length > 0) {
                try {
                    ByteArrayInputStream bais = new ByteArrayInputStream(currentProfilePicture);
                    Image image = new Image(bais);
                    profileImageView.setImage(image);
                    pictureStatusLabel.setText("(Current picture)");
                } catch (Exception e) {
                    System.out.println("Error loading profile picture: " + e.getMessage());
                }
            } else {
                pictureStatusLabel.setText("(No picture)");
            }
        }
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

        if (file != null) {
            selectedImageFile = file;
            try {
                currentProfilePicture = Files.readAllBytes(file.toPath());
                Image image = new Image(file.toURI().toString());
                profileImageView.setImage(image);
                pictureStatusLabel.setText("(Selected: " + file.getName() + ")");
            } catch (Exception e) {
                System.out.println("Error loading image: " + e.getMessage());
                messageLabel.setStyle("-fx-text-fill: red;");
                messageLabel.setText("Error loading image file");
            }
        }
    }

    @FXML
    private void handleSave() {
        User user = UserSession.getCurrentUser();
        if (user == null) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Session expired. Please log in again.");
            return;
        }

        String skills = skillsTextArea.getText().trim();
        String experience = experienceTextArea.getText().trim();

        try {
            userRepository.updateUserProfile(user.getId(), skills, experience, currentProfilePicture);
            
            user.setSkills(skills);
            user.setExperience(experience);
            user.setProfilePicture(currentProfilePicture);
            UserSession.setCurrentUser(user);

            messageLabel.setStyle("-fx-text-fill: green;");
            messageLabel.setText("Profile updated successfully!");
        } catch (SQLException e) {
            System.out.println("Error updating profile: " + e.getMessage());
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Error updating profile");
        }
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("dashboard.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("SkillSync - Dashboard");
        } catch (Exception e) {
            System.out.println("Error navigating to dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
