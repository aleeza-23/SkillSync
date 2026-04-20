import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ChatController {

    @FXML private VBox     messagesContainer;
    @FXML private ScrollPane scrollPane;
    @FXML private TextField  messageInput;
    @FXML private Button     backBtn;
    @FXML private Button     sendBtn;
    @FXML private Label      chatTitleLabel;
    @FXML private Label      chatSubtitleLabel;

    private int    otherUserId;
    private String otherUsername;

    @FXML
    public void initialize() {
        messageInput.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) handleSend();
        });
    }

    /** Called by any controller that opens this chat. */
    public void setConversation(int otherUserId, String otherUsername) {
        this.otherUserId   = otherUserId;
        this.otherUsername = otherUsername;
        chatTitleLabel.setText(otherUsername);
        chatSubtitleLabel.setText("SkillSync Messaging");
        loadMessages();
    }

    @FXML
    private void handleBack() {
        try {
            // Always go back to inbox so the conversation list is visible
            markMessagesRead();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("inbox.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) backBtn.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Messages");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSend() {
        String text = messageInput.getText().trim();
        if (text.isEmpty()) return;

        try {
            int myId = UserSession.getCurrentUser().getId();
            Connection conn = DatabaseManager.getConnection();

            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO MESSAGES (sender_id, receiver_id, content) VALUES (?, ?, ?)"
            );
            ps.setInt(1, myId);
            ps.setInt(2, otherUserId);
            ps.setString(3, text);
            ps.executeUpdate();

            messageInput.clear();
            loadMessages();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadMessages() {
        messagesContainer.getChildren().clear();

        try {
            int myId = UserSession.getCurrentUser().getId();
            Connection conn = DatabaseManager.getConnection();

            // Mark incoming messages as read
            markMessagesRead();

            String sql =
                "SELECT m.sender_id, m.content, " +
                "       CONVERT(NVARCHAR(20), m.sent_at, 120) AS sent_at_str " +
                "FROM MESSAGES m " +
                "WHERE (m.sender_id = ? AND m.receiver_id = ?) " +
                "   OR (m.sender_id = ? AND m.receiver_id = ?) " +
                "ORDER BY m.sent_at ASC";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, myId);
            ps.setInt(2, otherUserId);
            ps.setInt(3, otherUserId);
            ps.setInt(4, myId);

            ResultSet rs = ps.executeQuery();
            boolean hasMessages = false;

            while (rs.next()) {
                hasMessages = true;
                int    senderId = rs.getInt("sender_id");
                String content  = rs.getString("content");
                String sentAt   = rs.getString("sent_at_str");
                boolean isMine  = (senderId == myId);
                messagesContainer.getChildren().add(createBubble(content, sentAt, isMine));
            }

            if (!hasMessages) {
                Label empty = new Label("No messages yet — say hello!");
                empty.setStyle("-fx-font-size: 13px; -fx-text-fill: #b2bec3; -fx-padding: 20;");
                messagesContainer.getChildren().add(empty);
            }

            // Scroll to bottom after layout completes
            Platform.runLater(() -> scrollPane.setVvalue(1.0));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void markMessagesRead() {
        try {
            int myId = UserSession.getCurrentUser().getId();
            Connection conn = DatabaseManager.getConnection();
            PreparedStatement ps = conn.prepareStatement(
                "UPDATE MESSAGES SET is_read = 1 " +
                "WHERE receiver_id = ? AND sender_id = ? AND is_read = 0"
            );
            ps.setInt(1, myId);
            ps.setInt(2, otherUserId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private HBox createBubble(String content, String sentAt, boolean isMine) {
        Label bubble = new Label(content);
        bubble.setWrapText(true);
        bubble.setMaxWidth(370);

        if (isMine) {
            bubble.setStyle(
                "-fx-background-color: #273c75; -fx-text-fill: white;" +
                "-fx-padding: 10 14 10 14; -fx-background-radius: 18 18 4 18;" +
                "-fx-font-size: 13px;"
            );
        } else {
            bubble.setStyle(
                "-fx-background-color: white; -fx-text-fill: #2f3640;" +
                "-fx-padding: 10 14 10 14; -fx-background-radius: 18 18 18 4;" +
                "-fx-border-color: #dcdde1; -fx-border-radius: 18 18 18 4;" +
                "-fx-font-size: 13px;"
            );
        }

        // Time stamp below bubble
        String timeStr = (sentAt != null && sentAt.length() >= 16)
            ? sentAt.substring(11, 16) : "";
        Label timeLabel = new Label(timeStr);
        timeLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #b2bec3; -fx-padding: 1 4 0 4;");

        VBox msgBox = new VBox(2, bubble, timeLabel);
        msgBox.setAlignment(isMine ? Pos.TOP_RIGHT : Pos.TOP_LEFT);
        msgBox.setMaxWidth(400);

        HBox wrapper = new HBox(msgBox);
        wrapper.setAlignment(isMine ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        return wrapper;
    }
}
