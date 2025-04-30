package com.pastrymanagement.controller;

import com.pastrymanagement.model.Employee;

public class UserSession {
    private static UserSession instance;
    private Employee currentEmployee;

    private UserSession() {
        // Private constructor for singleton pattern
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
        this.currentEmployee = null;
    }

    public boolean isLoggedIn() {
        return currentEmployee != null;
    }

    public boolean isAdmin() {
        return isLoggedIn() && "Admin".equals(currentEmployee.getPosition());
    }

    public boolean isCashier() {
        return isLoggedIn() && "Cashier".equals(currentEmployee.getPosition());
    }

    public boolean isChef() {
        return isLoggedIn() && "Chef".equals(currentEmployee.getPosition());
    }
}