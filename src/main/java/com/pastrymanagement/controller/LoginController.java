package com.pastrymanagement.controller;

import com.pastrymanagement.PastryManagement;
import com.pastrymanagement.model.Employee;
import com.pastrymanagement.service.EmployeeService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Label errorLabel;
    @FXML private ImageView logoImageView;

    private EmployeeService employeeService;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize services
        employeeService = new EmployeeService();

        // Load logo image - try multiple paths
        try {
            // Try different paths to find the image
            String[] possiblePaths = {
                    "/images/img.png",
                    "/img.png",
                    "/com/pastrymanagement/images/img.png",
                    "/com/pastrymanagement/img.png"
            };

            InputStream imageStream = null;
            String foundPath = null;

            for (String path : possiblePaths) {
                System.out.println("Looking for logo image at: " + path);
                imageStream = getClass().getResourceAsStream(path);
                if (imageStream != null) {
                    foundPath = path;
                    break;
                }
            }

            if (imageStream == null) {
                System.err.println("Warning: Logo image not found. Using placeholder.");
            } else {
                System.out.println("Found logo image at: " + foundPath);
                Image logoImage = new Image(imageStream);
                logoImageView.setImage(logoImage);
            }
        } catch (Exception e) {
            System.err.println("Error loading logo: " + e.getMessage());
        }

        // Clear error message when user types
        emailField.textProperty().addListener((observable, oldValue, newValue) -> {
            errorLabel.setText("");
        });

        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            errorLabel.setText("");
        });
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter both email and password");
            return;
        }

        try {
            Employee employee = employeeService.authenticate(email, password);

            if (employee != null) {
                System.out.println("Login successful as: " + employee.getFullName() + " (" + employee.getPosition() + ")");

                // Store logged in user in session
                UserSession.getInstance().setCurrentEmployee(employee);

                // Navigate to main screen based on position
                PastryManagement.showMainScreen(employee.getPosition());
            } else {
                errorLabel.setText("Invalid email or password");
            }
        } catch (Exception e) {
            errorLabel.setText("Login error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}