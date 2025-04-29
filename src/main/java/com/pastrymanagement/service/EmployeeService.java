package com.pastrymanagement.service;

import com.pastrymanagement.model.Employee;
import com.pastrymanagement.repository.EmployeeRepository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmployeeService {
    private final EmployeeRepository employeeRepository;

    public EmployeeService() {
        this.employeeRepository = new EmployeeRepository();
    }

    public Employee authenticate(String email, String password) {
        try {
            // In a real application, you would hash the password before checking
            return employeeRepository.authenticate(email, password);
        } catch (SQLException e) {
            System.err.println("Authentication error: " + e.getMessage());
            return null;
        }
    }

    public List<Employee> getAllEmployees() {
        try {
            return employeeRepository.findAll();
        } catch (SQLException e) {
            System.err.println("Error fetching employees: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Employee> getNonAdminEmployees() {
        List<Employee> result = new ArrayList<>();
        try {
            for (Employee employee : employeeRepository.findAll()) {
                if (!"Admin".equals(employee.getPosition())) {
                    result.add(employee);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching non-admin employees: " + e.getMessage());
        }
        return result;
    }

    public Employee getEmployeeById(int id) {
        try {
            return employeeRepository.findById(id);
        } catch (SQLException e) {
            System.err.println("Error fetching employee by ID: " + e.getMessage());
            return null;
        }
    }

    public void addEmployee(Employee employee) {
        try {
            employeeRepository.create(employee);
        } catch (SQLException e) {
            System.err.println("Error adding employee: " + e.getMessage());
            throw new RuntimeException("Failed to add employee", e);
        }
    }

    public void updateEmployee(Employee employee) {
        try {
            employeeRepository.update(employee);
        } catch (SQLException e) {
            System.err.println("Error updating employee: " + e.getMessage());
            throw new RuntimeException("Failed to update employee", e);
        }
    }

    public void deleteEmployee(int id) {
        try {
            employeeRepository.delete(id);
        } catch (SQLException e) {
            System.err.println("Error deleting employee: " + e.getMessage());
        }
    }
}