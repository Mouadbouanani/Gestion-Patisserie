package com.pastrymanagement.controller;

import com.pastrymanagement.model.Employee;
import com.pastrymanagement.service.OrderService;
import com.pastrymanagement.service.ProductService;
import com.pastrymanagement.controller.UserSession;
import com.pastrymanagement.PastryManagement;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class EmployeeController implements Initializable {

    @FXML private TextField searchField;
    @FXML private Label usernameLabel;
    @FXML private Label positionLabel;
    @FXML private Button logoutButton;
    @FXML private ImageView sidebarLogo;
    @FXML private Label welcomeLabel;

    // Menu containers
    @FXML private VBox adminMenu;
    @FXML private VBox cashierMenu;
    @FXML private VBox chefMenu;

    // Content area
    @FXML private StackPane contentArea;

    //admin menu
    @FXML private TextField searchEmployeeField;
    @FXML private TableView<?> employeeTableView;
    @FXML private Button addEmployeeButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;
    @FXML private Button resetPasswordButton;
    @FXML private Button refreshButton;

    private Employee currentEmployee;
    private OrderService orderService;
    private ProductService productService;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize services
        orderService = new OrderService();
        productService = new ProductService();

        // Load current user from session
        currentEmployee = UserSession.getInstance().getCurrentEmployee();

        if (currentEmployee != null) {
            usernameLabel.setText(currentEmployee.getFullName());
            positionLabel.setText(currentEmployee.getPosition());
            welcomeLabel.setText("Welcome, " + currentEmployee.getFullName());

            // Initialize the appropriate menu based on position
            initializeByRole(currentEmployee.getPosition());
        }

        // Setup search functionality
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            // Implement search based on current view
        });
    }

    public void initializeByRole(String position) {
        // Hide all menus first
        adminMenu.setVisible(false);
        cashierMenu.setVisible(false);
        chefMenu.setVisible(false);

        // Show appropriate menu based on position
        switch (position) {
            case "Admin":
                adminMenu.setVisible(true);
                break;
            case "Cashier":
                cashierMenu.setVisible(true);
                break;
            case "Chef":
                chefMenu.setVisible(true);
                break;
            default:
                System.err.println("Unknown position: " + position);
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            // Clear session
            UserSession.getInstance().clearSession();

            // Return to login screen
            PastryManagement.showLoginScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Admin functionality
    @FXML
    private void showEmployeeManagement(ActionEvent event) {
        try {
            // Use the full package path
            String employeeMgmtPath = "/com/pastrymanagement/views/employee_management.fxml";
            URL resourceUrl = getClass().getResource(employeeMgmtPath);

            if (resourceUrl == null) {
                System.err.println("ERROR: Could not find employee_management.fxml at: " + employeeMgmtPath);
                return;
            }

            FXMLLoader loader = new FXMLLoader(resourceUrl);
            Parent view = loader.load();

            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
        } catch (IOException e) {
            System.err.println("Error loading employee management view:");
            e.printStackTrace();
        }
    }

    // Shared functionality
    @FXML
    private void showOrders(ActionEvent event) {
        loadView("/views/orders.fxml");
    }

    @FXML
    private void showProducts(ActionEvent event) {
        loadView("/views/products.fxml");
    }

    @FXML
    private void showReports(ActionEvent event) {
        loadView("/views/reports.fxml");
    }

    // Cashier functionality
    @FXML
    private void createNewOrder(ActionEvent event) {
        loadView("/views/new_order.fxml");
    }

    @FXML
    private void showClients(ActionEvent event) {
        loadView("/views/clients.fxml");
    }

    // Chef functionality
    @FXML
    private void showChefOrders(ActionEvent event) {
        loadView("/views/chef_orders.fxml");
    }

    @FXML
    private void showCompletedOrders(ActionEvent event) {
        loadView("/views/completed_orders.fxml");
    }


    private void loadView(String fxmlPath) {
        try {
            // Always use the full package path
            String fullPath = "/com/pastrymanagement/views/" + fxmlPath.replace("/views/", "");
            URL resourceUrl = getClass().getResource(fullPath);

            if (resourceUrl == null) {
                System.err.println("ERROR: Could not find " + fxmlPath + " at: " + fullPath);
                return;
            }

            FXMLLoader loader = new FXMLLoader(resourceUrl);
            Parent view = loader.load();

            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
        } catch (IOException e) {
            System.err.println("Error loading view: " + fxmlPath);
            e.printStackTrace();
        }
    }
}