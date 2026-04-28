import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class freelancerController {

    @FXML private Label topUsernameLabel;
    @FXML private Label welcomeLabel;
    @FXML private Label subtitleLabel;
    @FXML private ImageView topProfileImageView;
    @FXML private Button logoutBtn;
    @FXML private Button browseJobsBtn;
    @FXML private Button myApplicationsBtn;
    @FXML private Button editProfileBtn;
    @FXML private Button messagesBtn;

    @FXML
    public void initialize() {
        User user = UserSession.getCurrentUser();
        AvatarUtils.applyAvatar(topProfileImageView, null);
        subtitleLabel.setText("Freelancer Dashboard");
        welcomeLabel.setText("What do you want to do today?");
        if (user != null) {
            topUsernameLabel.setText("Hello, " + user.getUsername());
            AvatarUtils.applyAvatar(topProfileImageView, user.getProfilePicture());
        }
    }

    @FXML
    private void handleBrowseJobs() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("browse_jobs.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) browseJobsBtn.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Browse Jobs");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleMyApplications() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("my_applications.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) myApplicationsBtn.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("My Applications");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEditProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("profile.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) editProfileBtn.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("SkillSync - Edit Profile");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleMessages() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("inbox.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) messagesBtn.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Messages");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        try {
            Stage stage = (Stage) logoutBtn.getScene().getWindow();
            UserSession.logout();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.setTitle("SkillSync - Log In");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
