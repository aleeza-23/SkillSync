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

public class InboxController {

    @FXML private VBox conversationsContainer;
    @FXML private Button backBtn;

    @FXML
    public void initialize() {
        loadConversations();
    }

    @FXML
    private void handleBack() {
        try {
            String role = UserSession.getCurrentUser().getRole();
            boolean isFreelancer = "freelancer".equalsIgnoreCase(role);
            String fxml  = isFreelancer ? "freelancer_dashboard.fxml" : "client_dashboard.fxml";
            String title = isFreelancer ? "Freelancer Dashboard" : "Client Dashboard";

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) backBtn.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle(title);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadConversations() {
        conversationsContainer.getChildren().clear();
        try {
            int userId = UserSession.getCurrentUser().getId();
            Connection conn = DatabaseManager.getConnection();

            // CTE finds all unique conversation partners, subqueries pull the latest message per pair
            String sql =
                "WITH ConvPartners AS ( " +
                "    SELECT DISTINCT " +
                "        CASE WHEN sender_id = ? THEN receiver_id ELSE sender_id END AS other_id " +
                "    FROM MESSAGES " +
                "    WHERE sender_id = ? OR receiver_id = ? " +
                ") " +
                "SELECT cp.other_id, u.username AS other_username, " +
                "    (SELECT TOP 1 content FROM MESSAGES " +
                "     WHERE (sender_id = ? AND receiver_id = cp.other_id) " +
                "        OR (sender_id = cp.other_id AND receiver_id = ?) " +
                "     ORDER BY sent_at DESC) AS last_content, " +
                "    (SELECT TOP 1 CONVERT(NVARCHAR(20), sent_at, 120) FROM MESSAGES " +
                "     WHERE (sender_id = ? AND receiver_id = cp.other_id) " +
                "        OR (sender_id = cp.other_id AND receiver_id = ?) " +
                "     ORDER BY sent_at DESC) AS last_sent_at, " +
                "    (SELECT COUNT(*) FROM MESSAGES " +
                "     WHERE receiver_id = ? AND sender_id = cp.other_id AND is_read = 0) AS unread_count " +
                "FROM ConvPartners cp " +
                "JOIN dbo.users u ON u.id = cp.other_id " +
                "ORDER BY last_sent_at DESC";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setInt(2, userId);
            ps.setInt(3, userId);
            ps.setInt(4, userId);
            ps.setInt(5, userId);
            ps.setInt(6, userId);
            ps.setInt(7, userId);
            ps.setInt(8, userId);

            ResultSet rs = ps.executeQuery();
            boolean hasAny = false;

            while (rs.next()) {
                hasAny = true;
                int    otherId       = rs.getInt("other_id");
                String otherUsername = rs.getString("other_username");
                String lastContent   = rs.getString("last_content");
                String lastSentAt    = rs.getString("last_sent_at");
                int    unread        = rs.getInt("unread_count");

                String dateStr = (lastSentAt != null && lastSentAt.length() >= 10)
                    ? lastSentAt.substring(0, 10) : "";

                conversationsContainer.getChildren().add(
                    createConversationRow(otherId, otherUsername, lastContent, dateStr, unread)
                );
            }

            if (!hasAny) {
                Label empty = new Label("No messages yet.\nStart a conversation by clicking 'Message' on an application.");
                empty.setStyle("-fx-font-size: 13px; -fx-text-fill: #636e72; -fx-padding: 30; -fx-text-alignment: center;");
                empty.setWrapText(true);
                conversationsContainer.getChildren().add(empty);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Label err = new Label("Error loading conversations.");
            err.setStyle("-fx-text-fill: red; -fx-padding: 20;");
            conversationsContainer.getChildren().add(err);
        }
    }

    private VBox createConversationRow(int otherId, String otherUsername,
                                       String lastContent, String date, int unread) {
        VBox row = new VBox(4);
        row.setStyle(
            "-fx-background-color: white; -fx-padding: 14 16 14 16;" +
            "-fx-cursor: hand; -fx-border-color: transparent transparent #ecf0f1 transparent;" +
            "-fx-border-width: 0 0 1 0;"
        );

        // Name + unread badge
        Label nameLabel = new Label(otherUsername);
        nameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label dateLabel = new Label(date);
        dateLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #b2bec3;");

        HBox nameRow = new HBox(6, nameLabel, spacer);
        nameRow.setAlignment(Pos.CENTER_LEFT);

        if (unread > 0) {
            Label badge = new Label(String.valueOf(unread));
            badge.setStyle(
                "-fx-background-color: #273c75; -fx-text-fill: white;" +
                "-fx-font-size: 10px; -fx-font-weight: bold;" +
                "-fx-padding: 2 6 2 6; -fx-background-radius: 10;"
            );
            nameRow.getChildren().addAll(badge, dateLabel);
        } else {
            nameRow.getChildren().add(dateLabel);
        }

        // Preview line
        String preview = (lastContent != null && lastContent.length() > 65)
            ? lastContent.substring(0, 62) + "..."
            : (lastContent != null ? lastContent : "");
        Label previewLabel = new Label(preview);
        previewLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: "
            + (unread > 0 ? "#2f3640; -fx-font-weight: bold;" : "#636e72;"));

        row.getChildren().addAll(nameRow, previewLabel);

        // Hover effect
        row.setOnMouseEntered(e -> row.setStyle(
            "-fx-background-color: #eef2ff; -fx-padding: 14 16 14 16;" +
            "-fx-cursor: hand; -fx-border-color: transparent transparent #ecf0f1 transparent;" +
            "-fx-border-width: 0 0 1 0;"
        ));
        row.setOnMouseExited(e -> row.setStyle(
            "-fx-background-color: white; -fx-padding: 14 16 14 16;" +
            "-fx-cursor: hand; -fx-border-color: transparent transparent #ecf0f1 transparent;" +
            "-fx-border-width: 0 0 1 0;"
        ));

        row.setOnMouseClicked(e -> openChat(otherId, otherUsername));
        return row;
    }

    void openChat(int otherId, String otherUsername) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("chat.fxml"));
            Scene scene = new Scene(loader.load());

            ChatController ctrl = loader.getController();
            ctrl.setConversation(otherId, otherUsername);

            Stage stage = (Stage) backBtn.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Chat — " + otherUsername);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
