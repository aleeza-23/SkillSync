import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class clientController {

    @FXML
    private Label welcomeLabel;
    @FXML
    private Label subtitleLabel;
    @FXML
    private ImageView topProfileImageView;

    @FXML
    private Button postJobBtn;

    @FXML
    private Button myJobsBtn;

    @FXML
    private Button applicationsBtn;

    @FXML
    private Button messagesBtn;

    @FXML
    public void initialize() {
        User user = UserSession.getCurrentUser();
        welcomeLabel.setText("What do you want to do today?");
        subtitleLabel.setText("Client Dashboard");
        AvatarUtils.applyAvatar(topProfileImageView, null);
        if (user != null) {
            topUsernameLabel.setText("Hello, " + user.getUsername());
            AvatarUtils.applyAvatar(topProfileImageView, user.getProfilePicture());
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
    @FXML
    private Label topUsernameLabel;

    @FXML
    private Button logoutBtn;
    @FXML
    private Button editProfileBtn;
    @FXML
    private void handlePostJob() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("job_form.fxml"));
            Scene scene = new Scene(loader.load());

            Stage stage = new Stage();
            stage.setTitle("Post Job");
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleMyJobs() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("my_jobs.fxml"));
            Scene scene = new Scene(loader.load());

            Stage stage = (Stage) myJobsBtn.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("My Jobs");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleApplications() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("applications_received.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) applicationsBtn.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Applications Received");
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

}