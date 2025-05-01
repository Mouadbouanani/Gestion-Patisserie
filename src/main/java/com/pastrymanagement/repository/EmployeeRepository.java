package com.pastrymanagement.repository;


import com.pastrymanagement.model.Employee;
import com.pastrymanagement.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeRepository {

    // Create a new employee in the database
    public void create(Employee employee) throws SQLException {
        String sql = "INSERT INTO employee (full_name, position, email, password) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, employee.getFullName());
            stmt.setString(2, employee.getPosition());
            stmt.setString(3, employee.getEmail());
            stmt.setString(4, employee.getPassword());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating employee failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    employee.setEmployeeId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating employee failed, no ID obtained.");
                }
            }
        }
    }

    // Read an employee by ID
    public Employee findById(int id) throws SQLException {
        String sql = "SELECT * FROM employee WHERE employee_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEmployee(rs);
                }
            }
        }
        return null;
    }

    // Read an employee by email
    public Employee findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM employee WHERE email = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEmployee(rs);
                }
            }
        }
        return null;
    }

    // Read all employees
    public List<Employee> findAll() throws SQLException {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT * FROM employee";

        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                employees.add(mapResultSetToEmployee(rs));
            }
        }
        return employees;
    }

    // Update an existing employee
    public void update(Employee employee) throws SQLException {
        String sql = "UPDATE employee SET full_name = ?, position = ?, email = ?, password = ? WHERE employee_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, employee.getFullName());
            stmt.setString(2, employee.getPosition());
            stmt.setString(3, employee.getEmail());
            stmt.setString(4, employee.getPassword());
            stmt.setInt(5, employee.getEmployeeId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating employee failed, no rows affected.");
            }
        }
    }

    // Delete an employee
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM employee WHERE employee_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Deleting employee failed, no rows affected.");
            }
        }
    }

    // Authenticate an employee by email and password
    public Employee authenticate(String email, String password) throws SQLException {
        String sql = "SELECT * FROM employee WHERE email = ? AND password = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEmployee(rs);
                }
            }
        }
        return null;
    }

    // Helper method to map ResultSet to Employee object
    private Employee mapResultSetToEmployee(ResultSet rs) throws SQLException {
        Employee employee = new Employee();
        employee.setEmployeeId(rs.getInt("employee_id"));
        employee.setFullName(rs.getString("full_name"));
        employee.setPosition(rs.getString("position"));
        employee.setEmail(rs.getString("email"));
        employee.setPassword(rs.getString("password"));
        return employee;
    }
}