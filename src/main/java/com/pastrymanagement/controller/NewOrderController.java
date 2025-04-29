package com.pastrymanagement.controller;

import com.pastrymanagement.model.Client;
import com.pastrymanagement.model.Order;
import com.pastrymanagement.model.Product;
import com.pastrymanagement.service.ClientService;
import com.pastrymanagement.service.OrderService;
import com.pastrymanagement.service.ProductService;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class NewOrderController {

    @FXML private ComboBox<String> clientComboBox;
    @FXML private DatePicker orderDatePicker;
    @FXML private DatePicker deliveryDatePicker;
    @FXML private TextField notesField;

    @FXML private ComboBox<String> productCategoryComboBox;
    @FXML private ComboBox<Product> productComboBox;
    @FXML private Spinner<Integer> quantitySpinner;

    @FXML private TableView<OrderItem> orderItemsTableView;
    @FXML private TableColumn<OrderItem, Integer> productIdColumn;
    @FXML private TableColumn<OrderItem, String> productNameColumn;
    @FXML private TableColumn<OrderItem, String> priceColumn;
    @FXML private TableColumn<OrderItem, Integer> quantityColumn;
    @FXML private TableColumn<OrderItem, String> subtotalColumn;

    @FXML private Label itemsCountLabel;
    @FXML private Label subtotalLabel;
    @FXML private Label taxLabel;
    @FXML private Label totalLabel;

    @FXML private Button removeItemButton;
    @FXML private Button clearOrderButton;
    @FXML private Button cancelButton;
    @FXML private Button saveButton;
    @FXML private Button saveAndPrintButton;

    private ProductService productService;
    private OrderService orderService;
    private ObservableList<OrderItem> orderItems;
    private final BigDecimal TAX_RATE = new BigDecimal("0.15"); // 15% tax rate

    // Class to represent an item in the order
    public static class OrderItem {
        private final Product product;
        private final int quantity;
        private final BigDecimal subtotal;

        public OrderItem(Product product, int quantity) {
            this.product = product;
            this.quantity = quantity;
            this.subtotal = product.getUnitPrice().multiply(BigDecimal.valueOf(quantity));
        }

        public Product getProduct() {
            return product;
        }

        public int getQuantity() {
            return quantity;
        }

        public BigDecimal getSubtotal() {
            return subtotal;
        }
    }

    @FXML
    public void initialize() {
        // Initialize services
        productService = new ProductService();
        orderService = new OrderService();

        // Initialize order items list
        orderItems = FXCollections.observableArrayList();

        // Initialize date pickers
        orderDatePicker.setValue(LocalDate.now());
        deliveryDatePicker.setValue(LocalDate.now().plusDays(1));

        // Initialize product category combo box
        productCategoryComboBox.getItems().addAll("All", "Cakes", "Pastries", "Breads", "Tarts", "Others");
        productCategoryComboBox.getSelectionModel().selectFirst();

        // Initialize product combo box with all products
        refreshProductComboBox();

        // Initialize quantity spinner
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1);
        quantitySpinner.setValueFactory(valueFactory);

        // Initialize table columns
        productIdColumn.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getProduct().getProductId()).asObject());

        productNameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getProduct().getProductName()));

        priceColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty("$" + cellData.getValue().getProduct().getUnitPrice().toString()));

        quantityColumn.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getQuantity()).asObject());

        subtotalColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty("$" + cellData.getValue().getSubtotal().toString()));

        // Set table data
        orderItemsTableView.setItems(orderItems);

        // Add listener for product category changes
        productCategoryComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                refreshProductComboBox();
            }
        });

        // Add listener for table selection
        orderItemsTableView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> updateButtonStates(newSelection));

        // Initial button states
        updateButtonStates(null);
        updateOrderSummary();

        // Initialize sample client data
        clientComboBox.getItems().addAll("John Smith", "Sarah Johnson", "Mike Williams", "Emma Davis");
    }

    private void refreshProductComboBox() {
        List<Product> products = productService.getAllProducts();

        // Filter by category if needed
        String selectedCategory = productCategoryComboBox.getValue();
        if (selectedCategory != null && !selectedCategory.equals("All")) {
            // This is a mock implementation - in a real app, you'd have category in your Product model
            // and filter based on that
        }

        productComboBox.setItems(FXCollections.observableArrayList(products));

        // Set cell factory to display product name
        productComboBox.setCellFactory(param -> new ListCell<Product>() {
            @Override
            protected void updateItem(Product item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getProductName() + " - $" + item.getUnitPrice());
                }
            }
        });

        // Set converter to display selected product name
        productComboBox.setConverter(new javafx.util.StringConverter<Product>() {
            @Override
            public String toString(Product product) {
                if (product == null) {
                    return null;
                }
                return product.getProductName() + " - $" + product.getUnitPrice();
            }

            @Override
            public Product fromString(String string) {
                // Not needed for this application
                return null;
            }
        });
    }

    private void updateButtonStates(OrderItem selectedItem) {
        boolean isItemSelected = selectedItem != null;
        boolean hasItems = !orderItems.isEmpty();

        removeItemButton.setDisable(!isItemSelected);
        clearOrderButton.setDisable(!hasItems);
        saveButton.setDisable(!hasItems);
        saveAndPrintButton.setDisable(!hasItems);
    }

    private void updateOrderSummary() {
        int itemCount = 0;
        BigDecimal subtotal = BigDecimal.ZERO;

        for (OrderItem item : orderItems) {
            itemCount += item.getQuantity();
            subtotal = subtotal.add(item.getSubtotal());
        }

        BigDecimal tax = subtotal.multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.add(tax);

        itemsCountLabel.setText(String.valueOf(itemCount));
        subtotalLabel.setText("$" + subtotal.toString());
        taxLabel.setText("$" + tax.toString());
        totalLabel.setText("$" + total.toString());
    }

    @FXML
    private void handleAddClient() {
        ClientController.showClientFormDialog("Add New Client", null);
        // Refresh client list after adding
        refreshClientList();
    }

    private void refreshClientList() {
        ClientService clientService = new ClientService();
        List<Client> clients = clientService.getAllClients();
        clientComboBox.getItems().clear();
        for (Client client : clients) {
            clientComboBox.getItems().add(client.getFullName());
        }
    }

    @FXML
    private void handleAddToOrder() {
        Product selectedProduct = productComboBox.getValue();
        if (selectedProduct == null) {
            showAlert("Error", "Please select a product.");
            return;
        }

        int quantity = quantitySpinner.getValue();
        if (quantity <= 0) {
            showAlert("Error", "Please enter a valid quantity.");
            return;
        }

        // Check if there's enough quantity available
        if (selectedProduct.getAvailableQuantity() < quantity) {
            showAlert("Insufficient Stock",
                    "Not enough stock available. Only " + selectedProduct.getAvailableQuantity() + " units available.");
            return;
        }

        // Check if product is already in order
        for (int i = 0; i < orderItems.size(); i++) {
            OrderItem item = orderItems.get(i);
            if (item.getProduct().getProductId() == selectedProduct.getProductId()) {
                // Update existing item
                int newQuantity = item.getQuantity() + quantity;
                if (selectedProduct.getAvailableQuantity() < newQuantity) {
                    showAlert("Insufficient Stock",
                            "Not enough stock available. Only " + selectedProduct.getAvailableQuantity() + " units available.");
                    return;
                }

                orderItems.set(i, new OrderItem(selectedProduct, newQuantity));
                updateOrderSummary();
                return;
            }
        }

        // Add new item
        orderItems.add(new OrderItem(selectedProduct, quantity));
        updateButtonStates(null);
        updateOrderSummary();
    }

    @FXML
    private void handleRemoveItem() {
        OrderItem selectedItem = orderItemsTableView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            orderItems.remove(selectedItem);
            updateButtonStates(null);
            updateOrderSummary();
        }
    }

    @FXML
    private void handleClearOrder() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Clear");
        alert.setHeaderText("Clear Order");
        alert.setContentText("Are you sure you want to clear all items from this order?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            orderItems.clear();
            updateButtonStates(null);
            updateOrderSummary();
        }
    }

    @FXML
    private void handleCancel() {
        if (!orderItems.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Cancel");
            alert.setHeaderText("Cancel Order");
            alert.setContentText("Are you sure you want to cancel this order? All items will be lost.");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                closeWindow();
            }
        } else {
            closeWindow();
        }
    }

    @FXML
    private void handleSave() {
        if (validateOrder()) {
            saveOrder(false);
        }
    }

    @FXML
    private void handleSaveAndPrint() {
        if (validateOrder()) {
            saveOrder(true);
        }
    }

    private boolean validateOrder() {
        if (orderItems.isEmpty()) {
            showAlert("Error", "Cannot save an empty order. Please add items.");
            return false;
        }

        if (clientComboBox.getValue() == null || clientComboBox.getValue().trim().isEmpty()) {
            showAlert("Error", "Please select a client.");
            return false;
        }

        if (orderDatePicker.getValue() == null) {
            showAlert("Error", "Please select an order date.");
            return false;
        }

        if (deliveryDatePicker.getValue() == null) {
            showAlert("Error", "Please select a delivery date.");
            return false;
        }

        if (deliveryDatePicker.getValue().isBefore(orderDatePicker.getValue())) {
            showAlert("Error", "Delivery date cannot be before order date.");
            return false;
        }

        return true;
    }

    private void saveOrder(boolean printAfterSave) {
        // Get client ID from the selected client name
        String clientName = clientComboBox.getValue();
        ClientService clientService = new ClientService();
        List<Client> clients = clientService.searchClients(clientName);
        
        if (clients.isEmpty()) {
            showAlert("Error", "Could not find client information.");
            return;
        }
        
        Client client = clients.get(0); // Get the first matching client

        // Create a map of products and quantities
        Map<Product, Integer> orderProducts = new HashMap<>();
        for (OrderItem item : orderItems) {
            orderProducts.put(item.getProduct(), item.getQuantity());
        }

        // Create and save the order
        Order order = orderService.createOrder(client.getClientId(), orderProducts);
        
        if (order == null) {
            showAlert("Error", "Failed to save order to database.");
            return;
        }

        // Update product quantities
        boolean allSuccessful = true;
        for (OrderItem item : orderItems) {
            boolean success = productService.decreaseProductQuantity(item.getProduct(), item.getQuantity());
            if (!success) {
                allSuccessful = false;
                break;
            }
        }

        if (allSuccessful) {
            showAlert("Success", "Order saved successfully!");

            if (printAfterSave) {
                printOrder();
            }

            closeWindow();
        } else {
            // If product quantity update failed, we should rollback the order
            orderService.deleteOrder(order.getOrderId());
            showAlert("Error", "Failed to save order due to inventory update issues.");
        }
    }

    private void printOrder() {
        // This would implement printing functionality
        showAlert("Print", "Printing functionality would be implemented here.");
    }

    private void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}