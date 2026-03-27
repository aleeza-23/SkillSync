import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MyJobsController {

    @FXML
    private VBox jobsContainer;

    @FXML
    private Button backBtn;

    @FXML
    public void initialize() {
        loadJobs();
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("client_dashboard.fxml"));
            Scene scene = new Scene(loader.load());

            Stage stage = (Stage) backBtn.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Client Dashboard");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadJobs() {
        try {
            int clientId = UserSession.getCurrentUser().getId();

            Connection conn = DatabaseManager.getConnection();

            String sql = "SELECT * FROM JOBS WHERE client_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, clientId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                VBox card = createJobCard(
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getDouble("budget"),
                        rs.getDate("deadline").toString()
                );

                jobsContainer.getChildren().add(card);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private VBox createJobCard(String title, String desc, double budget, String deadline) {

        VBox card = new VBox(8);
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-padding: 15;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-radius: 10;" +
                        "-fx-border-color: #dcdde1;"
        );

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label descLabel = new Label(desc);
        Label budgetLabel = new Label("Budget: $" + budget);
        Label deadlineLabel = new Label("Deadline: " + deadline);

        card.getChildren().addAll(titleLabel, descLabel, budgetLabel, deadlineLabel);

        return card;
    }
}