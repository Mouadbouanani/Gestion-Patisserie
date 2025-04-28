
package com.gestionpatisserie.model;

import java.math.BigDecimal;
import java.util.Date;

public class Order {
    private int orderId;
    private Date orderDate;
    private BigDecimal amount;
    private String status;
    private int clientId;

    public Order(int clientId, String status, BigDecimal amount, Date orderDate, int orderId) {
        this.clientId = clientId;
        this.status = status;
        this.amount = amount;
        this.orderDate = orderDate;
        this.orderId = orderId;
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
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }
}