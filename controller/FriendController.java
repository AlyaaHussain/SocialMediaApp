package controller;

import dao.FriendDAO;
import dao.UserDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Friend;
import model.User;
import utils.SessionManager;

import java.util.List;

public class FriendController {

    @FXML private TextField searchField;
    @FXML private VBox searchResultsVBox;
    @FXML private VBox requestsVBox;
    @FXML private VBox friendsListVBox;

    private UserDAO userDAO = new UserDAO();
    private FriendDAO friendDAO = new FriendDAO();
    private User currentUser;
    private dao.NotificationDAO notificationDAO = new dao.NotificationDAO();

    @FXML
    public void initialize() {
        currentUser = SessionManager.getCurrentUser();
        if (currentUser != null) {
            loadRequests();
            loadFriends();
        }
    }

    @FXML
    public void handleSearch(ActionEvent event) {
        String query = searchField.getText().trim();
        if (query.isEmpty()) return;

        searchResultsVBox.getChildren().clear();
        try {
            List<User> results = userDAO.searchUsers(query, currentUser.getId());
            if (results.isEmpty()) {
                searchResultsVBox.getChildren().add(new Label("No users found."));
                return;
            }

            for (User u : results) {
                HBox card = createPersonCard(u);
                
                String status = friendDAO.checkStatus(currentUser.getId(), u.getId());
                Button actionBtn = new Button();
                
                if (status.equals("none") || status.equals("declined")) {
                    actionBtn.setText("Add Friend");
                    actionBtn.getStyleClass().add("btn-primary");
                    actionBtn.setOnAction(e -> sendRequest(u.getId(), actionBtn));
                } else if (status.equals("pending")) {
                    actionBtn.setText("Pending");
                    actionBtn.setDisable(true);
                } else if (status.equals("accepted")) {
                    actionBtn.setText("Friends ✓");
                    actionBtn.setDisable(true);
                }

                card.getChildren().add(actionBtn);
                searchResultsVBox.getChildren().add(card);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendRequest(int targetId, Button btn) {
        try {
            friendDAO.addFriend(new Friend(currentUser.getId(), targetId));
            // السطر الجديد لإرسال الإشعار
            notificationDAO.addNotification(new model.Notification(targetId, currentUser.getId(), "friend_request"));
            btn.setText("Pending");
            btn.setDisable(true);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadRequests() {
        requestsVBox.getChildren().clear();
        try {
            List<Integer> requesters = friendDAO.getPendingRequests(currentUser.getId());
            if (requesters.isEmpty()) {
                Label l = new Label("No new requests.");
                l.setStyle("-fx-text-fill: gray;");
                requestsVBox.getChildren().add(l);
                return;
            }

            for (Integer reqId : requesters) {
                User u = userDAO.getUserById(reqId);
                if (u != null) {
                    HBox card = createPersonCard(u);
                    
                    Button acceptBtn = new Button("Accept");
                    acceptBtn.getStyleClass().add("btn-accept");
                    acceptBtn.setOnAction(e -> respondToRequest(reqId, "accepted"));

                    Button declineBtn = new Button("Decline");
                    declineBtn.getStyleClass().add("btn-decline");
                    declineBtn.setOnAction(e -> respondToRequest(reqId, "declined"));

                    card.getChildren().addAll(acceptBtn, declineBtn);
                    requestsVBox.getChildren().add(card);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void respondToRequest(int requesterId, String status) {
        try {
            friendDAO.updateStatus(requesterId, currentUser.getId(), status);
            if (status.equals("accepted")) {
                // إرسال إشعار للمرسل أن طلبه قُبل
                notificationDAO.addNotification(new model.Notification(requesterId, currentUser.getId(), "friend_accept"));
                loadFriends();
            }
            loadRequests(); // Refresh requests
            if (status.equals("accepted")) loadFriends(); // Refresh friends list
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadFriends() {
        friendsListVBox.getChildren().clear();
        try {
            List<Friend> friends = friendDAO.getFriendsByUserId(currentUser.getId());
            // Need to also get friends where current user is the target (friend_id)
            // For simplicity, assuming your DAO handles both sides or we just display them
            
            if (friends.isEmpty()) {
                Label l = new Label("You have no friends yet.");
                l.setStyle("-fx-text-fill: gray;");
                friendsListVBox.getChildren().add(l);
                return;
            }

            for (Friend f : friends) {
                // Determine the other person's ID
                int otherId = (f.getUserId() == currentUser.getId()) ? f.getFriendId() : f.getUserId();
                User u = userDAO.getUserById(otherId);
                
                if (u != null) {
                    HBox card = createPersonCard(u);
                    Button msgBtn = new Button("Message"); // Placeholder for future feature
                    msgBtn.getStyleClass().add("btn-secondary");
                    card.getChildren().add(msgBtn);
                    friendsListVBox.getChildren().add(card);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // دالة مساعدة لإنشاء شكل الكارت الموحد
    private HBox createPersonCard(User u) {
        HBox card = new HBox(15);
        card.setAlignment(Pos.CENTER_LEFT);
        card.getStyleClass().add("friend-item");
        
        Label nameLbl = new Label(u.getName());
        nameLbl.getStyleClass().add("friend-name");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        card.getChildren().addAll(nameLbl, spacer);
        return card;
    }

    @FXML
    public void goBack(ActionEvent event) {
        try {
            Parent homeRoot = FXMLLoader.load(getClass().getResource("/view/home.fxml"));
            Stage stage = (Stage) searchField.getScene().getWindow();
            stage.setScene(new Scene(homeRoot));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}