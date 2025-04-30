package com.pastrymanagement;

import com.pastrymanagement.controller.EmployeeController;
import com.pastrymanagement.controller.ChefController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class PastryManagement extends Application {
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        showLoginScreen();
    }

    public static void showLoginScreen() throws IOException {
        // Debug: Print out where we're looking for the file
        String loginPath = "/login.fxml";
        URL loginUrl = PastryManagement.class.getResource(loginPath);
        System.out.println("Looking for login.fxml at: " + loginPath);
        System.out.println("Found login.fxml URL: " + loginUrl);

        if (loginUrl == null) {
            // Try an alternative path
            loginPath = "/com/pastrymanagement/login.fxml";
            loginUrl = PastryManagement.class.getResource(loginPath);
            System.out.println("Trying alternate path: " + loginPath);
            System.out.println("Found login.fxml URL: " + loginUrl);
        }

        if (loginUrl == null) {
            System.err.println("CRITICAL ERROR: Could not find login.fxml!");
            return;
        }

        // Load FXML
        FXMLLoader loader = new FXMLLoader(loginUrl);
        Parent root = loader.load();
        Scene scene = new Scene(root);

        // Try to load stylesheet
        URL stylesheetUrl = PastryManagement.class.getResource("/styles/styles.css");
        if (stylesheetUrl != null) {
            System.out.println("Found stylesheet at: " + stylesheetUrl);
            scene.getStylesheets().add(stylesheetUrl.toExternalForm());
        } else {
            System.out.println("Warning: Stylesheet not found at /styles/styles.css");

            // Try alternative path
            stylesheetUrl = PastryManagement.class.getResource("/com/pastrymanagement/styles/styles.css");
            if (stylesheetUrl != null) {
                System.out.println("Found stylesheet at alternate path: " + stylesheetUrl);
                scene.getStylesheets().add(stylesheetUrl.toExternalForm());
            }
        }

        primaryStage.setTitle("Pastry Management System - Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void showMainScreen(String employeePosition) throws IOException {
        String fxmlPath;
        if ("Chef".equalsIgnoreCase(employeePosition)) {
            fxmlPath = "/com/pastrymanagement/views/chef_main.fxml";
        } else {
            fxmlPath = "/com/pastrymanagement/views/main.fxml";
        }

        // Debug: Print out where we're looking for the file
        URL mainUrl = PastryManagement.class.getResource(fxmlPath);
        System.out.println("Looking for FXML at: " + fxmlPath);
        System.out.println("Found FXML URL: " + mainUrl);

        if (mainUrl == null) {
            System.err.println("Error: FXML file not found at " + fxmlPath);
            return;
        }

        FXMLLoader loader = new FXMLLoader(mainUrl);
        Parent root = loader.load();

        // Get controller and set employee position
        if ("Chef".equalsIgnoreCase(employeePosition)) {
            ChefController controller = loader.getController();
            // Initialize chef-specific settings if needed
        } else {
            EmployeeController controller = loader.getController();
            controller.initializeByRole(employeePosition);
        }

        Scene scene = new Scene(root);

        // Try to load stylesheet
        URL stylesheetUrl = PastryManagement.class.getResource("/com/pastrymanagement/styles/styles.css");
        if (stylesheetUrl != null) {
            System.out.println("Found stylesheet at: " + stylesheetUrl);
            scene.getStylesheets().add(stylesheetUrl.toExternalForm());
        } else {
            System.err.println("Warning: Stylesheet not found!");
        }

        primaryStage.setTitle("Pastry Management System - " + employeePosition);
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch();
    }
}