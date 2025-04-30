package com.pastrymanagement.controller;

import com.pastrymanagement.model.Order;
import com.pastrymanagement.model.Product;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class OrderDetailsController implements Initializable {

    @FXML private Label orderIdLabel;
    @FXML private Label dateLabel;
    @FXML private Label clientIdLabel;
    @FXML private Label statusLabel;
    @FXML private Label totalLabel;
    @FXML private TableView<Map.Entry<Product, Integer>> itemsTableView;
    @FXML private TableColumn<Map.Entry<Product, Integer>, String> productColumn;
    @FXML private TableColumn<Map.Entry<Product, Integer>, Integer> quantityColumn;
    @FXML private TableColumn<Map.Entry<Product, Integer>, String> priceColumn;

    private Order order;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configureTableColumns();
    }

    public void setOrder(Order order) {
        this.order = order;
        updateUI();
    }

    private void configureTableColumns() {
        productColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getKey().getProductName()));
        quantityColumn.setCellValueFactory(cellData -> 
            new SimpleIntegerProperty(cellData.getValue().getValue()).asObject());
        priceColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(String.format("$%.2f", 
                cellData.getValue().getKey().getUnitPrice().doubleValue() * cellData.getValue().getValue())));
    }

    private void updateUI() {
        if (order != null) {
            orderIdLabel.setText("Order #" + order.getOrderId());
            dateLabel.setText(order.getOrderDate().toString());
            clientIdLabel.setText("Client #" + order.getClientId());
            statusLabel.setText(order.getStatus());
            totalLabel.setText(String.format("$%.2f", order.getAmount()));

            // Update items table
            itemsTableView.getItems().clear();
            itemsTableView.getItems().addAll(order.getOrderProducts().entrySet());
        }
    }
} 