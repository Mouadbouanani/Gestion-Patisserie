
package com.pastrymanagement.model;


import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

public class Order {
    private int orderId;
    private Date orderDate;
    private BigDecimal amount;
    private OrderStatus status;
    private int clientId;
    private Map<Product, Integer> orderProducts;

    public Order() {
    }


    public Order(int clientId, BigDecimal amount, Date orderDate) {
        this.clientId = clientId;
        this.status = OrderStatus.PENDING;
        this.amount = amount;
        this.orderDate = orderDate;
        this.orderId = generateOrderId();
        this.orderProducts = new HashMap<>();
    }

    public Order(int clientId, String status, BigDecimal amount, int orderId, Map<Product, Integer> orderProducts) {
        this.clientId = clientId;
        this.status = OrderStatus.valueOf(status);
        this.amount = amount;
        this.orderDate = orderDate;
        this.orderId = orderId;
        this.orderProducts = orderProducts;
    }

    public Order(int clientId, BigDecimal amount, Date orderDate, OrderStatus status) {
        this.clientId = clientId;
        this.amount = amount;
        this.orderDate = orderDate;
        this.orderId = orderId;
        this.status = status;
        this.orderProducts = new HashMap<>();
    }

    public Map<Product, Integer> getOrderProducts() {
        return orderProducts;
    }

    public void setOrderProducts(Map<Product, Integer> orderProducts) {
        this.orderProducts = orderProducts;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status.name();
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public boolean addProduct(Product product, int quantity) {
        if (orderProducts.containsKey(product)) {
            orderProducts.put(product, orderProducts.get(product) + quantity);
            return true;
        } else {
            orderProducts.put(product, quantity);
            return true;
        }

    }

    public boolean removeProduct(Product product) {
        if (orderProducts.containsKey(product)) {
            orderProducts.remove(product);
            return true;
        } else {
            return false;
        }
    }

    public boolean modifyProductQuantity(Product product, int newQuantity) {
        if (orderProducts.containsKey(product)) {
            orderProducts.replace(product, orderProducts.get(product), newQuantity);
            this.amount = this.calculAmount();
            return true;
        }
        return false;
    }

    public void changeStatus(OrderStatus status) {
        this.status = status;
    }

    public void cancelOrder() {
        this.status = OrderStatus.CANCELLED;
    }

    public BigDecimal calculAmount() {
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (Map.Entry<Product, Integer> entry : orderProducts.entrySet()) {
            Product product = entry.getKey();
            int quantity = entry.getValue();
            totalAmount = totalAmount.add(product.getUnitPrice().multiply(BigDecimal.valueOf(quantity)));
        }
        return totalAmount;
    }
    public static int generateOrderId() {
        return (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
    }


}
