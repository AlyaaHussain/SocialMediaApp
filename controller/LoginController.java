package controller;

import dao.UserDAO;
import model.User;
import utils.SessionManager;
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

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private UserDAO userDAO = new UserDAO();

    @FXML
    public void handleLogin(ActionEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showError("Please fill in all fields.");
            return;
        }

        try {
            User user = userDAO.login(email, password);
            if (user != null) {
            	SessionManager.setCurrentUser(user);
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
            } else {
                showError("Invalid email or password.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Database error occurred.");
        }
    }

    @FXML
    public void goToRegister(MouseEvent event) {
        try {
            Parent registerRoot = FXMLLoader.load(getClass().getResource("/view/register.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(registerRoot));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setStyle("-fx-text-fill: #FF5F8F;"); // إعادة اللون الأحمر للخطأ
    }
}