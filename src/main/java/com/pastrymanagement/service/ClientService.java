package com.pastrymanagement.service;

import com.pastrymanagement.model.Client;
import com.pastrymanagement.repository.ClientRepository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClientService {
    private final ClientRepository clientRepository;

    public ClientService() {
        this.clientRepository = new ClientRepository();
    }

    public void addClient(Client client) {
        try {
            clientRepository.create(client);
        } catch (SQLException e) {
            System.err.println("Error adding client: " + e.getMessage());
            throw new RuntimeException("Failed to add client", e);
        }
    }

    public ArrayList<Client> getAllClients() {
        try {
            return clientRepository.findAll();
        } catch (SQLException e) {
            System.err.println("Error fetching clients: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public Client getClientById(int id) {
        try {
            return clientRepository.findById(id);
        } catch (SQLException e) {
            System.err.println("Error fetching client by ID: " + e.getMessage());
            return null;
        }
    }

    public void updateClient(Client client) {
        try {
            clientRepository.update(client);
        } catch (SQLException e) {
            System.err.println("Error updating client: " + e.getMessage());
            throw new RuntimeException("Failed to update client", e);
        }
    }

    public void deleteClient(int id) {
        try {
            clientRepository.delete(id);
        } catch (SQLException e) {
            System.err.println("Error deleting client: " + e.getMessage());
            throw new RuntimeException("Failed to delete client", e);
        }
    }

    public List<Client> searchClients(String keyword) {
        try {
            List<Client> allClients = clientRepository.findAll();
            List<Client> result = new ArrayList<>();
            String lowerKeyword = keyword.toLowerCase();

            for (Client client : allClients) {
                if (client.getFullName().toLowerCase().contains(lowerKeyword) ||
                        client.getAddress().toLowerCase().contains(lowerKeyword) ||
                        client.getPhoneNumbers().toLowerCase().contains(lowerKeyword) ||
                        String.valueOf(client.getClientId()).contains(keyword)) {
                    result.add(client);
                }
            }
            return result;
        } catch (SQLException e) {
            System.err.println("Error searching clients: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}