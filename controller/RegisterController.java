package controller;

import dao.UserDAO;
import dao.ProfileDAO;
import model.User;
import utils.SessionManager;
import model.Profile;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.Node;

public class RegisterController {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private UserDAO userDAO = new UserDAO();
    private ProfileDAO profileDAO = new ProfileDAO();

    @FXML
    public void handleRegister(ActionEvent event) {
        String name = nameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showError("Please fill in all fields.");
            return;
        }

        try {
            // 1. إنشاء المستخدم في قاعدة البيانات
            User newUser = new User(name, email, password);
            userDAO.register(newUser);

            // 2. خطوة احترافية: بعد إنشاء المستخدم، نقوم بعمل Login صامت لجلب الـ ID الخاص به
            User createdUser = userDAO.login(email, password);
            
            if (createdUser != null) {
            	SessionManager.setCurrentUser(createdUser);
            	try {
            	    // الانتقال للصفحة الرئيسية
            	    Parent homeRoot = FXMLLoader.load(getClass().getResource("/view/home.fxml"));
            	    Stage stage = (Stage) emailField.getScene().getWindow();
            	    stage.setScene(new Scene(homeRoot));
            	    stage.centerOnScreen();
            	} catch (Exception ex) {
            	    ex.printStackTrace();
            	    showError("Error loading home page.");
            	}
            }

        } catch (Exception e) {
            e.printStackTrace();
            showError("Registration failed. Email might already exist.");
        }
    }

    @FXML
    public void goToLogin(MouseEvent event) {
        try {
            Parent loginRoot = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(loginRoot));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setStyle("-fx-text-fill: #FF5F8F;"); // إعادة اللون الأحمر
    }
}