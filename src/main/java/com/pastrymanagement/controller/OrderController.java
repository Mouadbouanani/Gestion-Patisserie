package com.pastrymanagement.controller;

import com.pastrymanagement.model.Order;
import com.pastrymanagement.model.OrderStatus;
import com.pastrymanagement.model.Product;
import com.pastrymanagement.service.OrderService;
//import com.pastrymanagement.util.AlertUtil;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;
import java.util.ResourceBundle;

public class OrderController implements Initializable {

    @FXML private TextField searchOrderField;
    @FXML private ComboBox<OrderStatus> statusFilterComboBox;
    @FXML private DatePicker fromDatePicker;
    @FXML private DatePicker toDatePicker;
    @FXML private Button searchButton;
    @FXML private Button refreshButton;
    @FXML private TableView<Order> ordersTableView;
    @FXML private TableColumn<Order, Integer> orderIdColumn;
    @FXML private TableColumn<Order, Date> dateColumn;
    @FXML private TableColumn<Order, String> clientColumn;
    @FXML private TableColumn<Order, String> statusColumn;
    @FXML private TableColumn<Order, Integer> itemsColumn;
    @FXML private TableColumn<Order, BigDecimal> totalColumn;
    @FXML private TableColumn<Order, String> assignedToColumn;
    @FXML private Button viewDetailsButton;
    @FXML private Button updateStatusButton;
    @FXML private Button printInvoiceButton;

    private OrderService orderService;
    private ObservableList<Order> ordersList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize service
        orderService = new OrderService();

        // Configure status filter combobox
        statusFilterComboBox.setItems(FXCollections.observableArrayList(OrderStatus.values()));

        // Set default dates (last 30 days) only if date pickers exist
        if (fromDatePicker != null && toDatePicker != null) {
            fromDatePicker.setValue(LocalDate.now().minusDays(30));
            toDatePicker.setValue(LocalDate.now());
        }

        // Configure table columns
        configureTableColumns();

        // Load orders
        loadAllOrders();

        // Enable/disable buttons based on selection
        ordersTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            boolean hasSelection = newVal != null;
            viewDetailsButton.setDisable(!hasSelection);
            updateStatusButton.setDisable(!hasSelection);
            if (printInvoiceButton != null) {
                printInvoiceButton.setDisable(!hasSelection);
            }
        });
    }

    private void configureTableColumns() {
        orderIdColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getOrderId()).asObject());

        dateColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getOrderDate()));

        clientColumn.setCellValueFactory(cellData -> new SimpleStringProperty("Client #" + cellData.getValue().getClientId()));

        statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus()));

        itemsColumn.setCellValueFactory(cellData -> {
            int itemCount = 0;
            for (int quantity : cellData.getValue().getOrderProducts().values()) {
                itemCount += quantity;
            }
            return new SimpleIntegerProperty(itemCount).asObject();
        });

        totalColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getAmount()));

        // Only configure assignedToColumn if it exists in the FXML
        if (assignedToColumn != null) {
            assignedToColumn.setCellValueFactory(cellData -> new SimpleStringProperty("Not Assigned"));
        }
    }

    private void loadAllOrders() {
        // Use service to get all orders
        ArrayList<Order> orders = orderService.getAllOrders();
        ordersList = FXCollections.observableArrayList(orders);
        ordersTableView.setItems(ordersList);
    }

    @FXML
    private void handleSearch(ActionEvent event) {
        // Get filter values
        String orderIdStr = searchOrderField.getText().trim();
        OrderStatus selectedStatus = statusFilterComboBox.getValue();
        LocalDate fromDate = fromDatePicker.getValue();
        LocalDate toDate = toDatePicker.getValue();

        ArrayList<Order> filteredOrders = new ArrayList<>();

        // First handle order ID search if provided
        if (!orderIdStr.isEmpty()) {
            try {
                int orderId = Integer.parseInt(orderIdStr);
                // Use the service method to get order by ID
                Order order = orderService.getOrderById(orderId);
                if (order != null) {
                    filteredOrders.add(order);
                }
            } catch (NumberFormatException e) {
                AlertUtil.showError("Invalid Order ID", "Please enter a valid order ID number.");
                return;
            }
        }
        // If no order ID specified, apply other filters
        else {
            // Apply status filter if selected
            if (selectedStatus != null) {
                filteredOrders = orderService.getOrdersByStatus(selectedStatus);
            } else {
                filteredOrders = orderService.getAllOrders();
            }

            // Apply date range filter if both dates are provided
            if (fromDate != null && toDate != null) {
                Date sqlFromDate = Date.valueOf(fromDate);
                Date sqlToDate = Date.valueOf(toDate);
                // Use the service method to filter by date range
                filteredOrders = orderService.getOrdersByDateRange(sqlFromDate, sqlToDate);
            }
        }

        // Update table with filtered orders
        ordersList = FXCollections.observableArrayList(filteredOrders);
        ordersTableView.setItems(ordersList);
    }

    @FXML
    private void handleRefresh(ActionEvent event) {
        // Reset filters
        searchOrderField.clear();
        statusFilterComboBox.setValue(null);
        fromDatePicker.setValue(LocalDate.now().minusDays(30));
        toDatePicker.setValue(LocalDate.now());

        // Reload all orders
        loadAllOrders();
    }
    @FXML
    private void handleAddOrder(ActionEvent event) {
        try {
            // Load new order view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pastrymanagement/views/new_order.fxml"));
            Parent root = loader.load();

            // Show in new window
            Stage stage = new Stage();
            stage.setTitle("New Order");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            // Refresh table after dialog is closed
            loadAllOrders();

        } catch (IOException e) {
            e.printStackTrace();
            AlertUtil.showError("Error", "Could not open new order view.");
        }
    }

    @FXML
    private void handleViewDetails(ActionEvent event) {
        Order selectedOrder = ordersTableView.getSelectionModel().getSelectedItem();
        if (selectedOrder == null) {
            return;
        }

        try {
            // Load order details view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pastrymanagement/view/OrderDetailsView.fxml"));
            Parent root = loader.load();

            // Get controller and pass the selected order
            OrderDetailsController controller = loader.getController();
            controller.setOrder(selectedOrder);

            // Show in new window
            Stage stage = new Stage();
            stage.setTitle("Order Details - #" + selectedOrder.getOrderId());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            // Refresh table after dialog is closed
            loadAllOrders();

        } catch (IOException e) {
            e.printStackTrace();
            AlertUtil.showError("Error", "Could not open order details view.");
        }
    }

    @FXML
    private void handleUpdateStatus(ActionEvent event) {
        Order selectedOrder = ordersTableView.getSelectionModel().getSelectedItem();
        if (selectedOrder == null) {
            return;
        }

        // Create dialog for status update
        Dialog<OrderStatus> dialog = new Dialog<>();
        dialog.setTitle("Update Order Status");
        dialog.setHeaderText("Select new status for Order #" + selectedOrder.getOrderId());

        // Set button types
        ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

        // Create and add ComboBox
        ComboBox<OrderStatus> statusComboBox = new ComboBox<>();
        statusComboBox.setItems(FXCollections.observableArrayList(OrderStatus.values()));
        statusComboBox.setValue(OrderStatus.valueOf(selectedOrder.getStatus()));
        dialog.getDialogPane().setContent(statusComboBox);

        // Convert result
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == updateButtonType) {
                return statusComboBox.getValue();
            }
            return null;
        });

        // Show dialog and process result
        dialog.showAndWait().ifPresent(status -> {
            // Use service method to update status
            boolean updated = orderService.updateOrderStatus(selectedOrder.getOrderId(), status);
            if (updated) {
                AlertUtil.showInformation("Status Updated",
                        "Order #" + selectedOrder.getOrderId() + " status updated to " + status);
                loadAllOrders();
            } else {
                AlertUtil.showError("Update Failed", "Could not update the order status.");
            }
        });
    }

    @FXML
    private void handlePrintInvoice(ActionEvent event) {
        Order selectedOrder = ordersTableView.getSelectionModel().getSelectedItem();
        if (selectedOrder == null) {
            return;
        }

        // In a real application, this would generate and print an invoice
        // Here we're just showing a confirmation message
        AlertUtil.showInformation("Print Invoice",
                "Invoice for Order #" + selectedOrder.getOrderId() + " has been sent to the printer.");
    }

    // Order Details Controller class (would normally be in a separate file)
    public static class OrderDetailsController {
        private Order order;

        public void setOrder(Order order) {
            this.order = order;
            // Initialize UI elements with order details
        }

        // Implementation for the order details view controller
    }

    // Utility class for showing alerts (would normally be in a separate file)
    public static class AlertUtil {
        public static void showError(String title, String message) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        }

        public static void showInformation(String title, String message) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        }
    }
}