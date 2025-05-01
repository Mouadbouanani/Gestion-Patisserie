package com.pastrymanagement.controller;

import com.pastrymanagement.model.Client;
import com.pastrymanagement.service.ClientService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import java.util.Optional;

public class ClientController {

    // UI Components
    @FXML private TextField searchField;
    @FXML private Button searchButton;
    @FXML private Button refreshButton;
    @FXML private Button addClientButton;

    @FXML private TableView<Client> clientsTable;
    @FXML private TableColumn<Client, Integer> idColumn;
    @FXML private TableColumn<Client, String> nameColumn;
    @FXML private TableColumn<Client, String> addressColumn;
    @FXML private TableColumn<Client, String> phoneColumn;

    @FXML private Button viewDetailsButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;

    // Data
    private final ClientService clientService = new ClientService();
    private final ObservableList<Client> clientList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTableColumns();
        loadAllClients();
        setupTableSelectionListener();
        disableActionButtons();
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("clientId"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumbers"));

        // Format phone numbers column to wrap text
        phoneColumn.setCellFactory(tc -> {
            TableCell<Client, String> cell = new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        setText(item.replace(",", "\n"));
                    }
                }
            };
            return cell;
        });
    }

    private void loadAllClients() {
        clientList.setAll(clientService.getAllClients());
        clientsTable.setItems(clientList);
    }

    private void setupTableSelectionListener() {
        clientsTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        enableActionButtons();
                    } else {
                        disableActionButtons();
                    }
                }
        );
    }

    private void enableActionButtons() {
        viewDetailsButton.setDisable(false);
        editButton.setDisable(false);
        deleteButton.setDisable(false);
    }

    private void disableActionButtons() {
        viewDetailsButton.setDisable(true);
        editButton.setDisable(true);
        deleteButton.setDisable(true);
    }

    // Action Handlers
    @FXML
    private void handleSearch() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadAllClients();
        } else {
            clientList.setAll(clientService.searchClients(keyword));
        }
    }

    @FXML
    private void handleRefresh() {
        searchField.clear();
        loadAllClients();
    }

    @FXML
    private void handleAddClient() {
        showClientFormDialog("Add New Client", null);
    }

    @FXML
    private void handleViewDetails() {
        Client selected = clientsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showClientDetailsDialog(selected);
        }
    }

    @FXML
    private void handleEdit() {
        Client selected = clientsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showClientFormDialog("Edit Client", selected);
        }
    }

    @FXML
    private void handleDelete() {
        Client selected = clientsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if (showConfirmationDialog(
                    "Delete Client",
                    "Confirm Deletion",
                    "Are you sure you want to delete " + selected.getFullName() + "?")) {

                clientService.deleteClient(selected.getClientId());
                clientList.remove(selected);
                showInformationDialog("Success", "Client deleted successfully!");
            }
        }
    }

    // Dialog Helpers
    public static void showClientFormDialog(String title, Client client) {
        Dialog<Client> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText("Please enter client details");

        // Set up buttons
        ButtonType saveButtonType = new ButtonType(client == null ? "Add" : "Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Create form
        GridPane grid = createClientFormGrid(client);
        dialog.getDialogPane().setContent(grid);

        // Convert result to Client when save button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                Client resultClient = client != null ? client : new Client();
                resultClient.setFullName(((TextField) grid.lookup("#nameField")).getText());
                resultClient.setAddress(((TextField) grid.lookup("#addressField")).getText());
                resultClient.setPhoneNumbers(((TextField) grid.lookup("#phoneField")).getText());
                return resultClient;
            }
            return null;
        });

        // Process the result
        Optional<Client> result = dialog.showAndWait();
        result.ifPresent(client1 -> {
            ClientService clientService = new ClientService();
            if (client1.getClientId() == 0) { // New client
                clientService.addClient(client1);
            } else { // Existing client
                clientService.updateClient(client1);
            }
        });
    }

    private static GridPane createClientFormGrid(Client client) {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField();
        nameField.setId("nameField");
        nameField.setPromptText("Full Name");

        TextField addressField = new TextField();
        addressField.setId("addressField");
        addressField.setPromptText("Address");

        TextField phoneField = new TextField();
        phoneField.setId("phoneField");
        phoneField.setPromptText("Phone Numbers (comma separated)");

        if (client != null) {
            nameField.setText(client.getFullName());
            addressField.setText(client.getAddress());
            phoneField.setText(client.getPhoneNumbers());
        }

        grid.add(new Label("Full Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Address:"), 0, 1);
        grid.add(addressField, 1, 1);
        grid.add(new Label("Phone Numbers:"), 0, 2);
        grid.add(phoneField, 1, 2);

        return grid;
    }

    private void showClientDetailsDialog(Client client) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Client Details");
        alert.setHeaderText(client.getFullName());

        String content = String.format(
                "ID: %d\nName: %s\nAddress: %s\nPhone Numbers:\n%s",
                client.getClientId(),
                client.getFullName(),
                client.getAddress(),
                client.getPhoneNumbers().replace(",", "\n")
        );

        alert.setContentText(content);
        alert.showAndWait();
    }

    private boolean showConfirmationDialog(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    private void showInformationDialog(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showErrorDialog(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}