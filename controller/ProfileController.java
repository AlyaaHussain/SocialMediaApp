package controller;

import dao.ProfileDAO;
import dao.UserDAO;
import model.Profile;
import model.User;
import utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import java.io.File;

public class ProfileController {

    @FXML private ImageView profileImageView;
    @FXML private TextField nameField;
    @FXML private TextArea bioArea;
    @FXML private Label statusLabel;

    private UserDAO userDAO = new UserDAO();
    private ProfileDAO profileDAO = new ProfileDAO();
    private String selectedImagePath = null;
    private Profile currentProfile;

    @FXML
    public void initialize() {
        loadUserData();
    }

    private void loadUserData() {
        try {
            User user = SessionManager.getCurrentUser();
            nameField.setText(user.getName());

            currentProfile = profileDAO.getProfileByUserId(user.getId());
            if (currentProfile != null) {
                bioArea.setText(currentProfile.getBio());
                selectedImagePath = currentProfile.getProfilePicture();
                
                // تحميل الصورة إذا كانت موجودة
                if (selectedImagePath != null && !selectedImagePath.equals("default_avatar.png")) {
                    File file = new File(selectedImagePath);
                    if (file.exists()) {
                        profileImageView.setImage(new Image(file.toURI().toString()));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleUploadPicture(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            selectedImagePath = file.getAbsolutePath();
            profileImageView.setImage(new Image(file.toURI().toString()));
        }
    }

    @FXML
    public void handleSaveChanges(ActionEvent event) {
        try {
            User user = SessionManager.getCurrentUser();
            // تحديث الاسم
            user = new User(user.getId(), nameField.getText(), user.getEmail(), user.getPassword());
            userDAO.updateUser(user);
            SessionManager.setCurrentUser(user); // تحديث الجلسة

            // تحديث البروفايل (Bio والصورة)
            if (currentProfile != null) {
                Profile updatedProfile = new Profile(user.getId(), bioArea.getText(), selectedImagePath);
                profileDAO.updateProfile(updatedProfile);
            }

            statusLabel.setText("Profile updated successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Error updating profile.");
            statusLabel.setStyle("-fx-text-fill: #FF5F8F;");
        }
    }

    @FXML
    public void goBack(ActionEvent event) {
        try {
            Parent homeRoot = FXMLLoader.load(getClass().getResource("/view/home.fxml"));
            Stage stage = (Stage) nameField.getScene().getWindow();
            stage.setScene(new Scene(homeRoot));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}