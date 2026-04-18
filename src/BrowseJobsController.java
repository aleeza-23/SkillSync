import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class BrowseJobsController {

    @FXML private VBox jobsContainer;
    @FXML private Button backBtn;

    @FXML
    public void initialize() {
        loadJobs();
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("freelancer_dashboard.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) backBtn.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Freelancer Dashboard");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadJobs() {
        jobsContainer.getChildren().clear();
        try {
            int freelancerId = UserSession.getCurrentUser().getId();
            Connection conn = DatabaseManager.getConnection();

            String sql =
                "SELECT j.id, j.title, j.description, j.budget, j.deadline, u.username AS client_name " +
                "FROM JOBS j " +
                "JOIN dbo.users u ON j.client_id = u.id " +
                "WHERE j.id NOT IN " +
                "    (SELECT job_id FROM APPLICATIONS WHERE freelancer_id = ?) " +
                "ORDER BY j.id DESC";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, freelancerId);
            ResultSet rs = ps.executeQuery();

            boolean hasJobs = false;
            while (rs.next()) {
                hasJobs = true;
                int jobId        = rs.getInt("id");
                String title     = rs.getString("title");
                String desc      = rs.getString("description");
                double budget    = rs.getDouble("budget");
                String deadline  = rs.getDate("deadline") != null ? rs.getDate("deadline").toString() : "N/A";
                String clientName = rs.getString("client_name");

                jobsContainer.getChildren().add(
                    createJobCard(jobId, title, desc, budget, deadline, clientName)
                );
            }

            if (!hasJobs) {
                Label empty = new Label("No new jobs available right now. Check back soon!");
                empty.setStyle("-fx-font-size: 14px; -fx-text-fill: #636e72; -fx-padding: 20;");
                jobsContainer.getChildren().add(empty);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Label err = new Label("Error loading jobs. Please try again.");
            err.setStyle("-fx-text-fill: red; -fx-padding: 20;");
            jobsContainer.getChildren().add(err);
        }
    }

    private VBox createJobCard(int jobId, String title, String desc,
                               double budget, String deadline, String clientName) {
        VBox card = new VBox(8);
        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-padding: 15;" +
            "-fx-background-radius: 10;" +
            "-fx-border-radius: 10;" +
            "-fx-border-color: #dcdde1;"
        );

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label clientLabel = new Label("Posted by: " + clientName);
        clientLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #636e72;");

        Label descLabel = new Label(desc);
        descLabel.setWrapText(true);
        descLabel.setStyle("-fx-text-fill: #2f3640;");

        Label budgetLabel = new Label("Budget: $" + String.format("%.2f", budget));
        budgetLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #27ae60;");

        Label deadlineLabel = new Label("Deadline: " + deadline);
        deadlineLabel.setStyle("-fx-text-fill: #e17055;");

        Button applyBtn = new Button("Apply Now");
        applyBtn.setStyle(
            "-fx-background-color: #273c75; -fx-text-fill: white;" +
            "-fx-font-weight: bold; -fx-padding: 6 14 6 14; -fx-background-radius: 6;"
        );
        applyBtn.setOnAction(e -> openApplyForm(jobId, title));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox footer = new HBox(10, budgetLabel, deadlineLabel, spacer, applyBtn);
        footer.setStyle("-fx-padding: 5 0 0 0;");
        footer.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        card.getChildren().addAll(titleLabel, clientLabel, descLabel, footer);
        return card;
    }

    private void openApplyForm(int jobId, String jobTitle) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("apply_form.fxml"));
            Scene scene = new Scene(loader.load());

            ApplyFormController controller = loader.getController();
            controller.setJobInfo(jobId, jobTitle);

            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Apply for: " + jobTitle);
            dialog.setScene(scene);
            dialog.showAndWait();

            // Refresh list after dialog closes (removes job if applied)
            loadJobs();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
