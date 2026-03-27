import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;

public class PostJobController {

    @FXML
    private TextField titleField;

    @FXML
    private TextArea descField;

    @FXML
    private TextField budgetField;

    @FXML
    private DatePicker deadlinePicker;

    @FXML
    private Button submitButton;

    @FXML
    private Label messageLabel;

    @FXML
    public void initialize() {
        // optional setup if needed
    }

    @FXML
    private void handleSubmitJob() {

        String title = titleField.getText().trim();
        String desc = descField.getText().trim();
        String budgetText = budgetField.getText().trim();

        // validation
        if (title.isEmpty() || desc.isEmpty() || budgetText.isEmpty() || deadlinePicker.getValue() == null) {
            showMessage("Please fill all fields", "red");
            return;
        }

        double budget;

        try {
            budget = Double.parseDouble(budgetText);
        } catch (NumberFormatException e) {
            showMessage("Budget must be a number", "red");
            return;
        }

        try {
            int clientId = UserSession.getCurrentUser().getId();

            Connection conn = DatabaseManager.getConnection();

            String sql = "INSERT INTO JOBS (title, description, budget, deadline, client_id) VALUES (?, ?, ?, ?, ?)";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, title);
            ps.setString(2, desc);
            ps.setDouble(3, budget);
            ps.setDate(4, Date.valueOf(deadlinePicker.getValue()));
            ps.setInt(5, clientId);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                showMessage("Job posted successfully!", "green");
                Stage stage = (Stage) submitButton.getScene().getWindow();
                stage.close();
                clearForm();
            } else {
                showMessage("Failed to post job", "red");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showMessage("Database error occurred", "red");
        }
    }

    private void clearForm() {
        titleField.clear();
        descField.clear();
        budgetField.clear();
        deadlinePicker.setValue(null);
    }

    private void showMessage(String msg, String color) {
        if (messageLabel != null) {
            messageLabel.setText(msg);
            messageLabel.setStyle("-fx-text-fill: " + color + ";");
        } else {
            System.out.println(msg);
        }
    }
}