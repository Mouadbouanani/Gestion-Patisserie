package com.pastrymanagement.service;

import com.pastrymanagement.model.Product;
import com.pastrymanagement.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;

public class ProductService {
    private ProductRepository productRepository;

    public ProductService() {
        this.productRepository = new ProductRepository();
    }

    public List<Product> getAllProducts() {
        try {
            return productRepository.findAll();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public Product getProductById(int id) {
        try {
            return productRepository.findById(id);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void addProduct(Product product) {
        try {
            productRepository.create(product);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateProduct(Product product) {
        try {
            productRepository.update(product);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean updateProductQuantity(int productId, int quantityChange) {
        Product product = getProductById(productId);
        if (product != null) {
            int newQuantity = product.getAvailableQuantity() + quantityChange;
            if (newQuantity >= 0) {
                product.setAvailableQuantity(newQuantity);
                updateProduct(product);
                return true;
            }
        }
        return false;
    }

    public void deleteProduct(int id) {
        try {
            productRepository.delete(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Product> searchProducts(String keyword) {
        List<Product> allProducts = getAllProducts();
        List<Product> result = new ArrayList<>();
        String lowerKeyword = keyword.toLowerCase();

        for (Product product : allProducts) {
            if (product.getProductName().toLowerCase().contains(lowerKeyword) ||
                    String.valueOf(product.getProductId()).contains(keyword)) {
                result.add(product);
            }
        }
        return result;
    }

    public boolean decreaseProductQuantity(Product product, int quantity) {
        int newQuantity = product.getAvailableQuantity() - quantity;
        if (newQuantity >= 0) {
            return productRepository.updateAvailableQuantity(product, newQuantity);
        }
        return false;
    }

    public boolean increaseProductQuantity(Product product, int quantity) {
        int newQuantity = product.getAvailableQuantity() + quantity;
        return productRepository.updateAvailableQuantity(product, newQuantity);
    }
}
