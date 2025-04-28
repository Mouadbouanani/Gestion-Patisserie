package com.gestionpatisserie.model;

public class Employee {
    private int employeeId;
    private String fullName;
    private String position;
    private String email;
    private String password;

    public Employee(int employeeId, String fullName, String position, String email, String password) {
        this.employeeId = employeeId;
        this.fullName = fullName;
        this.position = position;
        this.email = email;
        this.password = password;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}