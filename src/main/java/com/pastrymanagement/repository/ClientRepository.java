package com.pastrymanagement.repository;


import com.pastrymanagement.model.Client;
import com.pastrymanagement.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClientRepository {

    // Create a new client in the database
    public void create(Client client) throws SQLException {
        String sql = "INSERT INTO client (full_name, address, phone_numbers) VALUES (?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, client.getFullName());
            stmt.setString(2, client.getAddress());
            stmt.setString(3, client.getPhoneNumbers());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating client failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    client.setClientId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating client failed, no ID obtained.");
                }
            }
        }
    }

    // Read a client by ID
    public Client findById(int id) throws SQLException {
        String sql = "SELECT * FROM client WHERE client_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToClient(rs);
                }
            }
        }
        return null;
    }

    // Read all clients
    public List<Client> findAll() throws SQLException {
        List<Client> clients = new ArrayList<>();
        String sql = "SELECT * FROM client";

        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                clients.add(mapResultSetToClient(rs));
            }
        }
        return clients;
    }

    // Update an existing client
    public void update(Client client) throws SQLException {
        String sql = "UPDATE client SET full_name = ?, address = ?, phone_numbers = ? WHERE client_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, client.getFullName());
            stmt.setString(2, client.getAddress());
            stmt.setString(3, client.getPhoneNumbers());
            stmt.setInt(4, client.getClientId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating client failed, no rows affected.");
            }
        }
    }

    // Delete a client
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM client WHERE client_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Deleting client failed, no rows affected.");
            }
        }
    }

    // Helper method to map ResultSet to Client object
    private Client mapResultSetToClient(ResultSet rs) throws SQLException {
        Client client = new Client();
        client.setClientId(rs.getInt("client_id"));
        client.setFullName(rs.getString("full_name"));
        client.setAddress(rs.getString("address"));
        client.setPhoneNumbers(rs.getString("phone_numbers"));
        return client;
    }
}
