package com.pastrymanagement.model;
public enum OrderStatus {

    PENDING("Order created, awaiting processing"),

    PROCESSING("Order is being processed"),

    COMPLETED("Order has been completed"),

    DELIVERED("Order delivered to customer"),

    CANCELLED("Order has been cancelled");



    private final String description;



    private OrderStatus(String description) {

        this.description = description;

    }



    public String getDescription() {

        return description;

    }



}