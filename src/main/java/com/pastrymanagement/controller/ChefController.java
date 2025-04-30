package com.pastrymanagement.controller;

import com.pastrymanagement.model.Order;
import com.pastrymanagement.model.OrderStatus;
import com.pastrymanagement.service.OrderService;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ChefController implements Initializable {

    @FXML private TableView<Order> ordersTableView;
    @FXML private TableColumn<Order, Integer> orderIdColumn;
    @FXML private TableColumn<Order, String> dateColumn;
    @FXML private TableColumn<Order, String> clientColumn;
    @FXML private TableColumn<Order, String> statusColumn;
    @FXML private TableColumn<Order, Integer> itemsColumn;
    @FXML private Button updateStatusButton;
    @FXML private Button viewDetailsButton;

    private OrderService orderService;
    private ObservableList<Order> ordersList;
    private boolean showCompletedOrders;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        orderService = new OrderService();
        configureTableColumns();
        loadOrders();
        
        // Enable/disable buttons based on selection
        ordersTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            boolean hasSelection = newVal != null;
            viewDetailsButton.setDisable(!hasSelection);
            updateStatusButton.setDisable(!hasSelection);
        });
    }

    public void setShowCompletedOrders(boolean showCompleted) {
        this.showCompletedOrders = showCompleted;
        loadOrders();
    }

    private void configureTableColumns() {
        orderIdColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getOrderId()).asObject());
        dateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getOrderDate().toString()));
        clientColumn.setCellValueFactory(cellData -> new SimpleStringProperty("Client #" + cellData.getValue().getClientId()));
        statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus()));
        itemsColumn.setCellValueFactory(cellData -> {
            int itemCount = 0;
            for (int quantity : cellData.getValue().getOrderProducts().values()) {
                itemCount += quantity;
            }
            return new SimpleIntegerProperty(itemCount).asObject();
        });
    }

    private void loadOrders() {
        OrderStatus targetStatus = showCompletedOrders ? OrderStatus.COMPLETED : OrderStatus.PENDING;
        ArrayList<Order> orders = orderService.getOrdersByStatus(targetStatus);
        ordersList = FXCollections.observableArrayList(orders);
        ordersTableView.setItems(ordersList);
    }

    @FXML
    private void handleUpdateStatus() {
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
            boolean updated = orderService.updateOrderStatus(selectedOrder.getOrderId(), status);
            if (updated) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Status Updated");
                alert.setHeaderText(null);
                alert.setContentText("Order #" + selectedOrder.getOrderId() + " status updated to " + status);
                alert.showAndWait();
                loadOrders();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Update Failed");
                alert.setHeaderText(null);
                alert.setContentText("Could not update the order status.");
                alert.showAndWait();
            }
        });
    }

    @FXML
    private void handleViewDetails() {
        Order selectedOrder = ordersTableView.getSelectionModel().getSelectedItem();
        if (selectedOrder == null) {
            return;
        }

        try {
            // Load order details view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pastrymanagement/views/order_details.fxml"));
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
            loadOrders();

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Could not open order details view.");
            alert.showAndWait();
        }
    }
} 