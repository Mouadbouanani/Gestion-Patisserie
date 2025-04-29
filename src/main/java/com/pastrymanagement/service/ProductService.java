package com.pastrymanagement.service;

import com.pastrymanagement.model.Product;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProductService {

    // Mock database
    private List<Product> products;

    public ProductService() {
        // Initialize with sample data
        products = new ArrayList<>();
        products.add(new Product(1, "Chocolate Cake", new BigDecimal("35.00"), 10));
        products.add(new Product(2, "Croissant", new BigDecimal("3.50"), 25));
        products.add(new Product(3, "Strawberry Tart", new BigDecimal("15.75"), 8));
        products.add(new Product(4, "Macaron Box (6)", new BigDecimal("18.00"), 15));
        products.add(new Product(5, "Baguette", new BigDecimal("2.50"), 20));
    }

    public List<Product> getAllProducts() {
        return new ArrayList<>(products);
    }

    public Product getProductById(int id) {
        for (Product product : products) {
            if (product.getProductId() == id) {
                return product;
            }
        }
        return null;
    }

    public void addProduct(Product product) {
        // Generate new ID in a real application
        int newId = products.size() + 1;
        product.setProductId(newId);
        products.add(product);
    }

    public void updateProduct(Product product) {
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getProductId() == product.getProductId()) {
                products.set(i, product);
                return;
            }
        }
    }

    public boolean updateProductQuantity(int productId, int quantityChange) {
        for (Product product : products) {
            if (product.getProductId() == productId) {
                int newQuantity = product.getAvailableQuantity() + quantityChange;
                if (newQuantity >= 0) {
                    product.setAvailableQuantity(newQuantity);
                    return true;
                } else {
                    return false; // Not enough quantity available
                }
            }
        }
        return false; // Product not found
    }

    public void deleteProduct(int id) {
        products.removeIf(product -> product.getProductId() == id);
    }

    public List<Product> searchProducts(String keyword) {
        List<Product> result = new ArrayList<>();
        String lowerKeyword = keyword.toLowerCase();

        for (Product product : products) {
            if (product.getProductName().toLowerCase().contains(lowerKeyword) ||
                    product.getProductId() == Integer.parseInt(keyword)) {
                result.add(product);
            }
        }
        return result;
    }
}
