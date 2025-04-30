package com.pastrymanagement.controller;

import com.pastrymanagement.model.Product;
import com.pastrymanagement.service.ProductService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;

public class ProductController implements Initializable {

    @FXML private TextField searchProductField;
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private Button searchButton;
    @FXML private Button refreshButton;
    @FXML private Button addProductButton;

    @FXML private TableView<Product> productsTableView;
    @FXML private TableColumn<Product, Integer> idColumn;
    @FXML private TableColumn<Product, String> nameColumn;
    @FXML private TableColumn<Product, BigDecimal> priceColumn;
    @FXML private TableColumn<Product, Integer> stockColumn;
    @FXML private TableColumn<Product, String> statusColumn;

    @FXML private Label totalProductsLabel;
    @FXML private Label activeProductsLabel;
    @FXML private Label outOfStockLabel;

    @FXML private Button viewDetailsButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;
    @FXML private Button updateStockButton;

    private ProductService productService;
    private ObservableList<Product> productList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        productService = new ProductService();
        productList = FXCollections.observableArrayList(productService.getAllProducts());

        // Initialize table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("productId"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        stockColumn.setCellValueFactory(new PropertyValueFactory<>("availableQuantity"));

        // Set up status column with custom cell factory
        statusColumn.setCellValueFactory(cellData -> {
            int quantity = cellData.getValue().getAvailableQuantity();
            return javafx.beans.binding.Bindings.createStringBinding(
                    () -> quantity > 0 ? "In Stock" : "Out of Stock"
            );
        });

        // Initialize category combo box
        categoryComboBox.getItems().addAll("All", "Cakes", "Pastries", "Breads", "Tarts", "Others");
        categoryComboBox.getSelectionModel().selectFirst();

        // Set up table data
        productsTableView.setItems(productList);

        // Update statistics
        updateStatistics();

        // Set up selection listener
        productsTableView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> updateButtonStates(newSelection)
        );

        // Disable buttons initially
        updateButtonStates(null);
    }

    private void updateButtonStates(Product selectedProduct) {
        boolean isSelected = selectedProduct != null;
        viewDetailsButton.setDisable(!isSelected);
        editButton.setDisable(!isSelected);
        deleteButton.setDisable(!isSelected);
        updateStockButton.setDisable(!isSelected);
    }

    private void updateStatistics() {
        int total = productList.size();
        int inStock = (int) productList.stream()
                .filter(p -> p.getAvailableQuantity() > 0)
                .count();

        totalProductsLabel.setText(String.valueOf(total));
        activeProductsLabel.setText(String.valueOf(inStock));
        outOfStockLabel.setText(String.valueOf(total - inStock));
    }

    @FXML
    private void handleSearch() {
        String keyword = searchProductField.getText().trim();
        String category = categoryComboBox.getValue();

        if (keyword.isEmpty() && "All".equals(category)) {
            productList.setAll(productService.getAllProducts());
        } else {
            productList.setAll(productService.searchProducts(keyword));
            // Additional filtering by category would go here
        }
        updateStatistics();
    }

    @FXML
    private void handleRefresh() {
        productList.setAll(productService.getAllProducts());
        searchProductField.clear();
        categoryComboBox.getSelectionModel().selectFirst();
        updateStatistics();
    }

    @FXML
    private void handleAddProduct() {
        // Create a dialog to add new product
        Dialog<Product> dialog = new Dialog<>();
        dialog.setTitle("Add New Product");
        dialog.setHeaderText("Enter product details");

        // Set up buttons
        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        // Create form
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField();
        nameField.setPromptText("Product Name");
        TextField priceField = new TextField();
        priceField.setPromptText("Price");
        TextField quantityField = new TextField();
        quantityField.setPromptText("Quantity");

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Price:"), 0, 1);
        grid.add(priceField, 1, 1);
        grid.add(new Label("Quantity:"), 0, 2);
        grid.add(quantityField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        // Convert result to Product when Add button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                try {
                    Product product = new Product();
                    product.setProductName(nameField.getText());
                    product.setUnitPrice(new BigDecimal(priceField.getText()));
                    product.setAvailableQuantity(Integer.parseInt(quantityField.getText()));
                    return product;
                } catch (NumberFormatException e) {
                    showAlert("Invalid Input", "Please enter valid numbers for price and quantity.");
                    return null;
                }
            }
            return null;
        });

        // Process the result
        dialog.showAndWait().ifPresent(product -> {
            productService.addProduct(product);
            productList.add(product);
            updateStatistics();
            showAlert("Success", "Product added successfully!");
        });
    }

    @FXML
    private void handleViewDetails() {
        Product selected = productsTableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showProductDetails(selected);
        }
    }

    @FXML
    private void handleEditProduct() {
        Product selected = productsTableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            editProduct(selected);
        }
    }

    @FXML
    private void handleDeleteProduct() {
        Product selected = productsTableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Deletion");
            alert.setHeaderText("Delete Product");
            alert.setContentText("Are you sure you want to delete " + selected.getProductName() + "?");

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    productService.deleteProduct(selected.getProductId());
                    productList.remove(selected);
                    updateStatistics();
                    showAlert("Success", "Product deleted successfully!");
                }
            });
        }
    }

    @FXML
    private void handleUpdateStock() {
        Product selected = productsTableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            updateStock(selected);
        }
    }

    private void showProductDetails(Product product) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Product Details");
        alert.setHeaderText(product.getProductName());
        alert.setContentText(
                "ID: " + product.getProductId() + "\n" +
                        "Name: " + product.getProductName() + "\n" +
                        "Price: $" + product.getUnitPrice() + "\n" +
                        "Quantity: " + product.getAvailableQuantity() + "\n" +
                        "Status: " + (product.getAvailableQuantity() > 0 ? "In Stock" : "Out of Stock")
        );
        alert.showAndWait();
    }

    private void editProduct(Product product) {
        Dialog<Product> dialog = new Dialog<>();
        dialog.setTitle("Edit Product");
        dialog.setHeaderText("Edit product details");

        // Set up buttons
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Create form
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField(product.getProductName());
        TextField priceField = new TextField(product.getUnitPrice().toString());
        TextField quantityField = new TextField(String.valueOf(product.getAvailableQuantity()));

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Price:"), 0, 1);
        grid.add(priceField, 1, 1);
        grid.add(new Label("Quantity:"), 0, 2);
        grid.add(quantityField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        // Convert result to Product when Save button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    product.setProductName(nameField.getText());
                    product.setUnitPrice(new BigDecimal(priceField.getText()));
                    product.setAvailableQuantity(Integer.parseInt(quantityField.getText()));
                    return product;
                } catch (NumberFormatException e) {
                    showAlert("Invalid Input", "Please enter valid numbers for price and quantity.");
                    return null;
                }
            }
            return null;
        });

        // Process the result
        dialog.showAndWait().ifPresent(updatedProduct -> {
            productService.updateProduct(updatedProduct);
            productsTableView.refresh();
            updateStatistics();
            showAlert("Success", "Product updated successfully!");
        });
    }

    private void updateStock(Product product) {
        Dialog<Integer> dialog = new Dialog<>();
        dialog.setTitle("Update Stock");
        dialog.setHeaderText("Update quantity for " + product.getProductName());

        // Set up buttons
        ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

        // Create form
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField quantityField = new TextField();
        quantityField.setPromptText("Enter quantity change (+/-)");

        grid.add(new Label("Current Stock:"), 0, 0);
        grid.add(new Label(String.valueOf(product.getAvailableQuantity())), 1, 0);
        grid.add(new Label("Quantity Change:"), 0, 1);
        grid.add(quantityField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        // Convert result to quantity change when Update button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == updateButtonType) {
                try {
                    return Integer.parseInt(quantityField.getText());
                } catch (NumberFormatException e) {
                    showAlert("Invalid Input", "Please enter a valid number for quantity change.");
                    return null;
                }
            }
            return null;
        });

        // Process the result
        dialog.showAndWait().ifPresent(quantityChange -> {
            if (productService.updateProductQuantity(product.getProductId(), quantityChange)) {
                product.setAvailableQuantity(product.getAvailableQuantity() + quantityChange);
                productsTableView.refresh();
                updateStatistics();
                showAlert("Success", "Stock updated successfully!");
            } else {
                showAlert("Error", "Failed to update stock. Check if there's enough quantity available.");
            }
        });
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}