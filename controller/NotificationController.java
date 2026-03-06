package controller;

import dao.NotificationDAO;
import dao.UserDAO;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Notification;
import model.User;
import utils.SessionManager;

import java.util.List;

public class NotificationController {

    @FXML private VBox notificationsVBox;
    
    private NotificationDAO notifDAO = new NotificationDAO();
    private UserDAO userDAO = new UserDAO();

    @FXML
    public void initialize() {
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) return;

        new Thread(() -> {
            try {
                List<Notification> notifications = notifDAO.getNotificationsByUserId(currentUser.getId());
                
                Platform.runLater(() -> {
                    if (notifications.isEmpty()) {
                        Label l = new Label("No new notifications.");
                        l.setStyle("-fx-text-fill: gray; -fx-font-size: 16px;");
                        notificationsVBox.getChildren().add(l);
                        return;
                    }

                    for (Notification n : notifications) {
                        try {
                            User sender = userDAO.getUserById(n.getSenderId());
                            String senderName = (sender != null) ? sender.getName() : "Someone";
                            
                            // تحديد نص الإشعار بناءً على نوعه
                            String message = "";
                            if (n.getType().equals("friend_request")) message = senderName + " sent you a friend request.";
                            else if (n.getType().equals("friend_accept")) message = senderName + " accepted your friend request.";
                            else message = senderName + " interacted with you.";

                            HBox card = new HBox(10);
                            card.setAlignment(Pos.CENTER_LEFT);
                            card.setStyle("-fx-background-color: #252525; -fx-padding: 15; -fx-background-radius: 8;");
                            
                            Label textLabel = new Label("🔔 " + message);
                            textLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
                            
                            card.getChildren().add(textLabel);
                            notificationsVBox.getChildren().add(card);
                        } catch (Exception ex) { ex.printStackTrace(); }
                    }
                });
            } catch (Exception e) { e.printStackTrace(); }
        }).start();
    }

    @FXML
    public void goBack(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/home.fxml"));
            Stage stage = (Stage) notificationsVBox.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) { e.printStackTrace(); }
    }
}