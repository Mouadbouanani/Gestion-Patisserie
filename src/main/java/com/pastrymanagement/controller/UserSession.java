package com.pastrymanagement.controller;

import com.pastrymanagement.model.Employee;

public class UserSession {
    private static UserSession instance;
    private Employee currentEmployee;

    private UserSession() {
        // Private constructor to prevent instantiation
    }

    public static synchronized UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public Employee getCurrentEmployee() {
        return currentEmployee;
    }

    public void setCurrentEmployee(Employee employee) {
        this.currentEmployee = employee;
    }

    public void clearSession() {
        currentEmployee = null;
    }

    public boolean isLoggedIn() {
        return currentEmployee != null;
    }

    public boolean isAdmin() {
        return currentEmployee != null && "Admin".equals(currentEmployee.getPosition());
    }

    public boolean isCashier() {
        return currentEmployee != null && "Cashier".equals(currentEmployee.getPosition());
    }

    public boolean isChef() {
        return currentEmployee != null && "Chef".equals(currentEmployee.getPosition());
    }
}