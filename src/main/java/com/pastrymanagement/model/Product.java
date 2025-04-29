package com.pastrymanagement.model;

import java.math.BigDecimal;

public class Product {
    private int productId;
    private String productName;
    private BigDecimal unitPrice; // Consider using BigDecimal instead of String for prices
    private int availableQuantity;
    public Product() {
        this(0, "", BigDecimal.ZERO, 0);
    }

    public Product(int productId, String productName, BigDecimal unitPrice, int availableQuantity) {
        this.productId = productId;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.availableQuantity = availableQuantity;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(int availableQuantity) {
        this.availableQuantity = availableQuantity;
    }
}