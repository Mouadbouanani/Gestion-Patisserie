package com.pastrymanagement.model;

public class Client {
    private int clientId;
    private String fullName;
    private String address;
    private String phoneNumbers;

    public Client(int clientId, String fullName, String address, String phoneNumbers) {
        this.clientId = clientId;
        this.fullName = fullName;
        this.address = address;
        this.phoneNumbers = phoneNumbers;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(String phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }
}