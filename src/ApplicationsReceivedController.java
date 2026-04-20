import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ApplicationsReceivedController {

    @FXML private VBox applicationsContainer;
    @FXML private Button backBtn;

    @FXML
    public void initialize() {
        loadApplications();
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

    private void loadApplications() {
        applicationsContainer.getChildren().clear();
        try {
            int clientId = UserSession.getCurrentUser().getId();
            Connection conn = DatabaseManager.getConnection();

            String sql =
                "SELECT a.id, a.freelancer_id, a.status, a.cover_letter, a.applied_at, " +
                "       j.id AS job_id, j.title AS job_title, j.budget, j.deadline, " +
                "       u.username AS freelancer_name, u.email AS freelancer_email, " +
                "       u.skills AS freelancer_skills " +
                "FROM APPLICATIONS a " +
                "JOIN JOBS j ON a.job_id = j.id " +
                "JOIN dbo.users u ON a.freelancer_id = u.id " +
                "WHERE j.client_id = ? " +
                "ORDER BY j.id DESC, a.id DESC";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, clientId);
            ResultSet rs = ps.executeQuery();

            String lastJobTitle = null;
            boolean hasAny = false;

            while (rs.next()) {
                hasAny = true;

                int    appId          = rs.getInt("id");
                int    freelancerId   = rs.getInt("freelancer_id");
                String status         = rs.getString("status");
                String coverLetter    = rs.getString("cover_letter");
                String appliedAt      = rs.getString("applied_at");
                String jobTitle       = rs.getString("job_title");
                double budget         = rs.getDouble("budget");
                String deadline       = rs.getDate("deadline") != null ? rs.getDate("deadline").toString() : "N/A";
                String freelancerName = rs.getString("freelancer_name");
                String freelancerEmail= rs.getString("freelancer_email");
                String skills         = rs.getString("freelancer_skills");

                // Section header per job
                if (!jobTitle.equals(lastJobTitle)) {
                    if (lastJobTitle != null) {
                        applicationsContainer.getChildren().add(new Separator());
                    }
                    Label jobHeader = new Label("Job: " + jobTitle
                            + "  |  Budget: $" + String.format("%.0f", budget)
                            + "  |  Deadline: " + deadline);
                    jobHeader.setStyle(
                        "-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #636e72;" +
                        "-fx-padding: 8 0 4 0;"
                    );
                    applicationsContainer.getChildren().add(jobHeader);
                    lastJobTitle = jobTitle;
                }

                applicationsContainer.getChildren().add(
                    createApplicationCard(appId, freelancerId, freelancerName,
                                          freelancerEmail, skills, coverLetter, appliedAt, status)
                );
            }

            if (!hasAny) {
                Label empty = new Label("No applications received yet. Post a job to get started!");
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

    private VBox createApplicationCard(int appId, int freelancerId, String freelancerName,
                                       String email, String skills, String coverLetter,
                                       String appliedAt, String status) {
        VBox card = new VBox(8);
        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-padding: 15;" +
            "-fx-background-radius: 10;" +
            "-fx-border-radius: 10;" +
            "-fx-border-color: #dcdde1;"
        );

        // Name + status badge
        Label nameLabel = new Label(freelancerName);
        nameLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        String statusColor = statusColor(status);
        Label statusBadge = new Label(status.toUpperCase());
        statusBadge.setStyle(
            "-fx-background-color: " + statusColor + ";" +
            "-fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: bold;" +
            "-fx-padding: 3 8 3 8; -fx-background-radius: 12;"
        );

        Region nameSpacer = new Region();
        HBox.setHgrow(nameSpacer, Priority.ALWAYS);
        HBox nameRow = new HBox(10, nameLabel, nameSpacer, statusBadge);
        nameRow.setAlignment(Pos.CENTER_LEFT);

        // Email
        Label emailLabel = new Label(email);
        emailLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #636e72;");

        // Skills
        Label skillsHeader = new Label("Skills:");
        skillsHeader.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #2f3640;");
        Label skillsLabel = new Label(skills != null && !skills.isBlank() ? skills : "—");
        skillsLabel.setWrapText(true);
        skillsLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #636e72;");

        // Cover letter
        Label clHeader = new Label("Cover Letter:");
        clHeader.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #2f3640;");
        Label clLabel = new Label(coverLetter != null ? coverLetter : "");
        clLabel.setWrapText(true);
        clLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #2f3640;");

        // Applied date
        String dateStr = (appliedAt != null && appliedAt.length() >= 10) ? appliedAt.substring(0, 10) : "";
        Label dateLabel = new Label("Applied: " + dateStr);
        dateLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #b2bec3;");

        card.getChildren().addAll(nameRow, emailLabel, skillsHeader, skillsLabel, clHeader, clLabel, dateLabel);

        // Bottom action row: Accept/Reject (pending only) + Message button (always)
        HBox actionRow = new HBox(10);
        actionRow.setStyle("-fx-padding: 6 0 0 0;");
        actionRow.setAlignment(Pos.CENTER_LEFT);

        if ("pending".equalsIgnoreCase(status)) {
            Button acceptBtn = new Button("✓ Accept");
            acceptBtn.setStyle(
                "-fx-background-color: #27ae60; -fx-text-fill: white;" +
                "-fx-font-weight: bold; -fx-padding: 6 14 6 14; -fx-background-radius: 6;"
            );
            Button rejectBtn = new Button("✕ Reject");
            rejectBtn.setStyle(
                "-fx-background-color: #e74c3c; -fx-text-fill: white;" +
                "-fx-font-weight: bold; -fx-padding: 6 14 6 14; -fx-background-radius: 6;"
            );
            acceptBtn.setOnAction(e -> updateStatus(appId, "accepted", card, statusBadge, acceptBtn, rejectBtn));
            rejectBtn.setOnAction(e -> updateStatus(appId, "rejected", card, statusBadge, acceptBtn, rejectBtn));
            actionRow.getChildren().addAll(acceptBtn, rejectBtn);
        }

        // Message button — always available
        Region actionSpacer = new Region();
        HBox.setHgrow(actionSpacer, Priority.ALWAYS);

        Button msgBtn = new Button("✉ Message");
        msgBtn.setStyle(
            "-fx-background-color: transparent; -fx-text-fill: #273c75;" +
            "-fx-font-weight: bold; -fx-border-color: #273c75; -fx-border-radius: 6;" +
            "-fx-padding: 5 12 5 12; -fx-cursor: hand;"
        );
        msgBtn.setOnAction(e -> openChatWith(freelancerId, freelancerName));

        actionRow.getChildren().addAll(actionSpacer, msgBtn);
        card.getChildren().add(actionRow);

        return card;
    }

    private void openChatWith(int freelancerId, String freelancerName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("chat.fxml"));
            Scene scene = new Scene(loader.load());

            ChatController ctrl = loader.getController();
            ctrl.setConversation(freelancerId, freelancerName);

            Stage stage = (Stage) backBtn.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Chat — " + freelancerName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateStatus(int appId, String newStatus, VBox card,
                              Label statusBadge, Button acceptBtn, Button rejectBtn) {
        try {
            Connection conn = DatabaseManager.getConnection();
            PreparedStatement ps = conn.prepareStatement(
                "UPDATE APPLICATIONS SET status = ? WHERE id = ?"
            );
            ps.setString(1, newStatus);
            ps.setInt(2, appId);
            ps.executeUpdate();

            statusBadge.setText(newStatus.toUpperCase());
            statusBadge.setStyle(
                "-fx-background-color: " + statusColor(newStatus) + ";" +
                "-fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: bold;" +
                "-fx-padding: 3 8 3 8; -fx-background-radius: 12;"
            );
            // Remove Accept/Reject buttons but keep Message button
            card.getChildren().removeIf(node ->
                node instanceof HBox && ((HBox) node).getChildren().contains(acceptBtn)
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String statusColor(String status) {
        switch (status.toLowerCase()) {
            case "accepted": return "#27ae60";
            case "rejected": return "#e74c3c";
            default:         return "#f39c12";
        }
    }
}
