package com.pastrymanagement.controller;
import com.pastrymanagement.model.Employee;
import com.pastrymanagement.repository.EmployeeRepository;
import com.pastrymanagement.controller.UserSession;
import com.pastrymanagement.service.EmployeeService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class EmployeeManagementController {
    @FXML private TextField searchEmployeeField;
    @FXML private ComboBox<String> filterPositionComboBox;
    @FXML private TableView<Employee> employeeTableView;
    @FXML private TableColumn<Employee, Integer> idColumn;
    @FXML private TableColumn<Employee, String> nameColumn;
    @FXML private TableColumn<Employee, String> positionColumn;
    @FXML private TableColumn<Employee, String> emailColumn;
    @FXML private Button addEmployeeButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;
    @FXML private Button resetPasswordButton;
    @FXML private Button refreshButton;

    private EmployeeRepository employeeRepository;
    private ObservableList<Employee> employeeList;
    private EmployeeService employeeService;

    @FXML
    public void initialize() {
        employeeRepository = new EmployeeRepository();


        // Initialize table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("employeeId"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        positionColumn.setCellValueFactory(new PropertyValueFactory<>("position"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        // Initialize position filter combobox
        filterPositionComboBox.getItems().addAll("All", "Admin", "Cashier", "Chef");
        filterPositionComboBox.setValue("All");

        // Load employee data
        loadEmployeeData();

        // Set button visibility based on admin status
        boolean isAdmin = UserSession.getInstance().isAdmin();
        addEmployeeButton.setVisible(isAdmin);
        editButton.setVisible(isAdmin);
        deleteButton.setVisible(isAdmin);
        resetPasswordButton.setVisible(isAdmin);

        // Set up table selection listener
        employeeTableView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    editButton.setDisable(newSelection == null);
                    deleteButton.setDisable(newSelection == null);
                    resetPasswordButton.setDisable(newSelection == null);
                });
    }

    private void loadEmployeeData() {
        try {
            List<Employee> employees = employeeRepository.findAll();
            employeeList = FXCollections.observableArrayList(employees);
            employeeTableView.setItems(employeeList);
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to load employees: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddEmployee() {
        // Create a dialog to add new employee
        Dialog<Employee> dialog = new Dialog<>();
        dialog.setTitle("Add New Employee");
        dialog.setHeaderText("Enter employee details");

        // Set up dialog buttons
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Create form fields
        TextField fullNameField = new TextField();
        fullNameField.setPromptText("Full Name");
//        TextField usernameField = new TextField();
//        usernameField.setPromptText("Username");
        ComboBox<String> positionCombo = new ComboBox<>();
        positionCombo.getItems().addAll("Admin", "Cashier", "Chef");
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
//        TextField phoneField = new TextField();
//        phoneField.setPromptText("Phone");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        grid.add(new Label("Full Name:"), 0, 0);
        grid.add(fullNameField, 1, 0);
//        grid.add(new Label("Username:"), 0, 1);
//        grid.add(usernameField, 1, 1);
        grid.add(new Label("Position:"), 0, 2);
        grid.add(positionCombo, 1, 2);
        grid.add(new Label("Email:"), 0, 3);
        grid.add(emailField, 1, 3);
        grid.add(new Label("Password:"), 0, 4);
        grid.add(passwordField, 1, 4);
//        grid.add(new Label("Phone:"), 0, 5);
//        grid.add(phoneField, 1, 5);

        dialog.getDialogPane().setContent(grid);

        // Convert result to Employee when Save button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                Employee employee = new Employee();
                employee.setFullName(fullNameField.getText());
//                employee.setUsername(usernameField.getText());
                employee.setPosition(positionCombo.getValue());
                employee.setEmail(emailField.getText());
                employee.setPassword(passwordField.getText());
//                employee.setPhone(phoneField.getText());
//                employee.setStatus("Active");
                return employee;
            }
            return null;
        });

        Optional<Employee> result = dialog.showAndWait();

        result.ifPresent(employee -> {
            try {
                employeeRepository.create(employee);
                loadEmployeeData(); // Refresh table
                showAlert("Success", "Employee added successfully", Alert.AlertType.INFORMATION);
            } catch (SQLException e) {
                showAlert("Error", "Failed to add employee: " + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        });
    }

    @FXML
    private void handleEditEmployee() {
        Employee selectedEmployee = employeeTableView.getSelectionModel().getSelectedItem();
        if (selectedEmployee == null) {
            showAlert("No Selection", "Please select an employee to edit", Alert.AlertType.WARNING);
            return;
        }

        // Create a dialog to edit employee
        Dialog<Employee> dialog = new Dialog<>();
        dialog.setTitle("Edit Employee");
        dialog.setHeaderText("Edit employee details");

        // Set up dialog buttons
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Create form fields with current values
        TextField fullNameField = new TextField(selectedEmployee.getFullName());
//        TextField usernameField = new TextField(selectedEmployee.getUsername());
        ComboBox<String> positionCombo = new ComboBox<>();
        positionCombo.getItems().addAll("Admin", "Cashier", "Chef");
        positionCombo.setValue(selectedEmployee.getPosition());
        TextField emailField = new TextField(selectedEmployee.getEmail());
//        TextField phoneField = new TextField(selectedEmployee.getPhone());
        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll("Active", "Inactive");
//        statusCombo.setValue(selectedEmployee.getStatus());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        grid.add(new Label("Full Name:"), 0, 0);
        grid.add(fullNameField, 1, 0);
        grid.add(new Label("Position:"), 0, 2);
        grid.add(positionCombo, 1, 2);
        grid.add(new Label("Email:"), 0, 3);
        grid.add(emailField, 1, 3);

        dialog.getDialogPane().setContent(grid);

        // Convert result to Employee when Save button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                selectedEmployee.setFullName(fullNameField.getText());
                selectedEmployee.setPosition(positionCombo.getValue());
                selectedEmployee.setEmail(emailField.getText());
                return selectedEmployee;
            }
            return null;
        });

        Optional<Employee> result = dialog.showAndWait();

        result.ifPresent(employee -> {
            try {
                employeeRepository.update(employee);
                loadEmployeeData(); // Refresh table
                showAlert("Success", "Employee updated successfully", Alert.AlertType.INFORMATION);
            } catch (SQLException e) {
                showAlert("Error", "Failed to update employee: " + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        });
    }

    @FXML
    private void handleDeleteEmployee() {
        Employee selectedEmployee = employeeTableView.getSelectionModel().getSelectedItem();
        if (selectedEmployee == null) {
            showAlert("No Selection", "Please select an employee to delete", Alert.AlertType.WARNING);
            return;
        }

        // Confirm deletion
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Delete Employee");
        alert.setContentText("Are you sure you want to delete " + selectedEmployee.getFullName() + "?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                employeeRepository.delete(selectedEmployee.getEmployeeId());
                loadEmployeeData(); // Refresh table
                showAlert("Success", "Employee deleted successfully", Alert.AlertType.INFORMATION);
            } catch (SQLException e) {
                showAlert("Error", "Failed to delete employee: " + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleResetPassword() {
        Employee selectedEmployee = employeeTableView.getSelectionModel().getSelectedItem();
        if (selectedEmployee == null) {
            showAlert("No Selection", "Please select an employee to reset password", Alert.AlertType.WARNING);
            return;
        }

        // Create a dialog to reset password
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Reset Password");
        dialog.setHeaderText("Reset password for " + selectedEmployee.getFullName());

        // Set up dialog buttons
        ButtonType saveButtonType = new ButtonType("Reset", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Create form fields
        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("New Password");
        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm Password");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        grid.add(new Label("New Password:"), 0, 0);
        grid.add(newPasswordField, 1, 0);
        grid.add(new Label("Confirm Password:"), 0, 1);
        grid.add(confirmPasswordField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        // Convert result to password when Reset button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                if (!newPasswordField.getText().equals(confirmPasswordField.getText())) {
                    showAlert("Error", "Passwords do not match", Alert.AlertType.ERROR);
                    return null;
                }
                return newPasswordField.getText();
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(newPassword -> {
            try {
                selectedEmployee.setPassword(newPassword);
                employeeRepository.update(selectedEmployee);
                showAlert("Success", "Password reset successfully", Alert.AlertType.INFORMATION);
            } catch (SQLException e) {
                showAlert("Error", "Failed to reset password: " + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        });
    }

    @FXML
    private void handleRefresh() {
        String keyword = searchEmployeeField.getText().toLowerCase();
        String positionFilter = filterPositionComboBox.getValue();

        ObservableList<Employee> filteredList = employeeList.filtered(employee -> {
            boolean matchesKeyword = employee.getFullName().toLowerCase().contains(keyword)
                    || employee.getEmail().toLowerCase().contains(keyword);
            boolean matchesPosition = positionFilter.equals("All") || employee.getPosition().equals(positionFilter);
            return matchesKeyword && matchesPosition;
        });

        employeeTableView.setItems(filteredList);
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}