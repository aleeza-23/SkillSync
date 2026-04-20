import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class MyApplicationsController {

    @FXML private VBox applicationsContainer;
    @FXML private Button backBtn;

    @FXML
    public void initialize() {
        loadApplications();
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

    private void loadApplications() {
        try {
            int freelancerId = UserSession.getCurrentUser().getId();
            Connection conn = DatabaseManager.getConnection();

            String sql =
                "SELECT a.id, a.status, a.cover_letter, a.applied_at, " +
                "       j.title AS job_title, j.budget, j.deadline, j.client_id, " +
                "       u.username AS client_name " +
                "FROM APPLICATIONS a " +
                "JOIN JOBS j ON a.job_id = j.id " +
                "JOIN dbo.users u ON j.client_id = u.id " +
                "WHERE a.freelancer_id = ? " +
                "ORDER BY a.id DESC";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, freelancerId);
            ResultSet rs = ps.executeQuery();

            boolean hasApplications = false;
            while (rs.next()) {
                hasApplications = true;
                String jobTitle    = rs.getString("job_title");
                double budget      = rs.getDouble("budget");
                String deadline    = rs.getDate("deadline") != null ? rs.getDate("deadline").toString() : "N/A";
                String status      = rs.getString("status");
                String coverLetter = rs.getString("cover_letter");
                int    clientId    = rs.getInt("client_id");
                String clientName  = rs.getString("client_name");
                String appliedAt   = rs.getString("applied_at");

                applicationsContainer.getChildren().add(
                    createApplicationCard(jobTitle, budget, deadline, status,
                                          coverLetter, clientId, clientName, appliedAt)
                );
            }

            if (!hasApplications) {
                Label empty = new Label("You haven't applied to any jobs yet. Browse jobs to get started!");
                empty.setStyle("-fx-font-size: 14px; -fx-text-fill: #636e72; -fx-padding: 20;");
                applicationsContainer.getChildren().add(empty);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Label err = new Label("Error loading applications. Please try again.");
            err.setStyle("-fx-text-fill: red; -fx-padding: 20;");
            applicationsContainer.getChildren().add(err);
        }
    }

    private VBox createApplicationCard(String jobTitle, double budget, String deadline,
                                       String status, String coverLetter,
                                       int clientId, String clientName, String appliedAt) {
        VBox card = new VBox(8);
        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-padding: 15;" +
            "-fx-background-radius: 10;" +
            "-fx-border-radius: 10;" +
            "-fx-border-color: #dcdde1;"
        );

        // Job title + status badge
        Label titleLabel = new Label(jobTitle);
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        String statusColor;
        switch (status.toLowerCase()) {
            case "accepted": statusColor = "#27ae60"; break;
            case "rejected": statusColor = "#e74c3c"; break;
            default:         statusColor = "#f39c12"; break;
        }
        Label statusLabel = new Label(status.toUpperCase());
        statusLabel.setStyle(
            "-fx-background-color: " + statusColor + ";" +
            "-fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: bold;" +
            "-fx-padding: 3 8 3 8; -fx-background-radius: 12;"
        );

        Region titleSpacer = new Region();
        HBox.setHgrow(titleSpacer, Priority.ALWAYS);
        HBox titleRow = new HBox(10, titleLabel, titleSpacer, statusLabel);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        Label clientLabel = new Label("Client: " + clientName);
        clientLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #636e72;");

        Label budgetLabel = new Label("Budget: $" + String.format("%.2f", budget));
        budgetLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #27ae60;");

        Label deadlineLabel = new Label("Deadline: " + deadline);
        deadlineLabel.setStyle("-fx-text-fill: #e17055;");

        HBox metaRow = new HBox(15, budgetLabel, deadlineLabel);
        metaRow.setAlignment(Pos.CENTER_LEFT);

        Label coverLabel = new Label("Cover Letter: " + (coverLetter != null ? coverLetter : ""));
        coverLabel.setWrapText(true);
        coverLabel.setStyle("-fx-text-fill: #2f3640; -fx-font-size: 12px;");

        String dateStr = (appliedAt != null && appliedAt.length() >= 10) ? appliedAt.substring(0, 10) : "";
        Label appliedLabel = new Label("Applied on: " + dateStr);
        appliedLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #b2bec3;");

        // Message button — always visible so freelancer can contact client
        Button msgBtn = new Button("✉ Message Client");
        msgBtn.setStyle(
            "-fx-background-color: transparent; -fx-text-fill: #273c75;" +
            "-fx-font-weight: bold; -fx-border-color: #273c75; -fx-border-radius: 6;" +
            "-fx-padding: 5 12 5 12; -fx-cursor: hand;"
        );
        msgBtn.setOnAction(e -> openChatWith(clientId, clientName));

        HBox bottomRow = new HBox(msgBtn);
        bottomRow.setStyle("-fx-padding: 4 0 0 0;");
        bottomRow.setAlignment(Pos.CENTER_RIGHT);

        card.getChildren().addAll(titleRow, clientLabel, metaRow, coverLabel, appliedLabel, bottomRow);
        return card;
    }

    private void openChatWith(int clientId, String clientName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("chat.fxml"));
            Scene scene = new Scene(loader.load());

            ChatController ctrl = loader.getController();
            ctrl.setConversation(clientId, clientName);

            Stage stage = (Stage) backBtn.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Chat — " + clientName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
