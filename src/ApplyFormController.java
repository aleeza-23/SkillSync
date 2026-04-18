import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class ApplyFormController {

    @FXML private Label jobTitleLabel;
    @FXML private TextArea coverLetterField;
    @FXML private Label messageLabel;

    private int jobId;

    public void setJobInfo(int jobId, String jobTitle) {
        this.jobId = jobId;
        jobTitleLabel.setText("Apply for: " + jobTitle);
    }

    @FXML
    private void handleSubmit() {
        String coverLetter = coverLetterField.getText().trim();

        if (coverLetter.isEmpty()) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Please write a cover letter before submitting.");
            return;
        }

        try {
            int freelancerId = UserSession.getCurrentUser().getId();
            Connection conn = DatabaseManager.getConnection();

            String sql = "INSERT INTO APPLICATIONS (job_id, freelancer_id, cover_letter) VALUES (?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, jobId);
            ps.setInt(2, freelancerId);
            ps.setString(3, coverLetter);
            ps.executeUpdate();

            messageLabel.setStyle("-fx-text-fill: green;");
            messageLabel.setText("Application submitted successfully!");

            // Close dialog after a brief moment
            Stage stage = (Stage) coverLetterField.getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            e.printStackTrace();
            String msg = e.getMessage();
            if (msg != null && msg.contains("UQ_application")) {
                messageLabel.setStyle("-fx-text-fill: red;");
                messageLabel.setText("You have already applied for this job.");
            } else {
                messageLabel.setStyle("-fx-text-fill: red;");
                messageLabel.setText("Error submitting application. Please try again.");
            }
        }
    }
}
