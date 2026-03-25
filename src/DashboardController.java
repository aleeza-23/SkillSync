import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import java.io.ByteArrayInputStream;
import java.sql.SQLException;

public class DashboardController {

    private final UserRepository userRepository = new UserRepository();

    @FXML
    private Label usernameLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private Label skillsLabel;

    @FXML
    private Label experienceLabel;

    @FXML
    private ImageView profileImageView;

    @FXML
    private Button editProfileButton;

    @FXML
    private Button logoutButton;

    @FXML
    public void initialize() {
        User user = UserSession.getCurrentUser();
        if (user != null) {
            usernameLabel.setText("Username: " + user.getUsername());
            emailLabel.setText("Email: " + user.getEmail());
            skillsLabel.setText(user.getSkills() != null ? user.getSkills() : "(No skills added yet)");
            experienceLabel.setText(user.getExperience() != null ? user.getExperience() : "(No experience added yet)");

            if (user.getProfilePicture() != null && user.getProfilePicture().length > 0) {
                try {
                    ByteArrayInputStream bais = new ByteArrayInputStream(user.getProfilePicture());
                    Image image = new Image(bais);
                    profileImageView.setImage(image);
                } catch (Exception e) {
                    System.out.println("Error loading profile picture: " + e.getMessage());
                }
            }
        }
    }

    @FXML
    private void handleEditProfile() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("profile.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            Stage stage = (Stage) editProfileButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("SkillSync - Edit Profile");
        } catch (Exception e) {
            System.out.println("Error navigating to profile edit: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        UserSession.logout();
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("SkillSync - Log In");
        } catch (Exception e) {
            System.out.println("Error navigating to login: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
