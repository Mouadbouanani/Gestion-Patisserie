package com.pastrymanagement.repository;

import com.pastrymanagement.model.Product;
import com.pastrymanagement.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductRepository {

    // Create a new product in the database
    public void create(Product product) throws SQLException {
        String sql = "INSERT INTO product (product_name, unit_price, available_quantity) VALUES (?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, product.getProductName());
            stmt.setBigDecimal(2, product.getUnitPrice());
            stmt.setInt(3, product.getAvailableQuantity());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating product failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    product.setProductId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating product failed, no ID obtained.");
                }
            }
        }
    }

    // Read a product by ID
    public Product findById(int id) throws SQLException {
        String sql = "SELECT * FROM product WHERE product_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToProduct(rs);
                }
            }
        }
        return null;
    }

    // Read all products
    public List<Product> findAll() throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM product";

        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
        }
        return products;
    }

    // Update an existing product
    public void update(Product product) throws SQLException {
        String sql = "UPDATE product SET product_name = ?, unit_price = ?, available_quantity = ? WHERE product_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, product.getProductName());
            stmt.setBigDecimal(2, product.getUnitPrice());
            stmt.setInt(3, product.getAvailableQuantity());
            stmt.setInt(4, product.getProductId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating product failed, no rows affected.");
            }
        }
    }

    // Delete a product
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM product WHERE product_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Deleting product failed, no rows affected.");
            }
        }
    }

    // Helper method to map ResultSet to Product object
    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setProductId(rs.getInt("product_id"));
        product.setProductName(rs.getString("product_name"));
        product.setUnitPrice(rs.getBigDecimal("unit_price"));
        product.setAvailableQuantity(rs.getInt("available_quantity"));
        return product;
    }
}