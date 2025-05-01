package com.pastrymanagement.service;

import com.pastrymanagement.model.Order;
import com.pastrymanagement.model.OrderStatus;
import com.pastrymanagement.model.Product;
import com.pastrymanagement.repository.OrderRepository;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;

public class OrderService {
    private OrderRepository orderRepository;

    public void AddOrder(Order order) {
        orderRepository.addOrder(order);
    }

    public OrderService() {
        this.orderRepository = new OrderRepository();
    }


    public Order createOrder(int clientId, Map<Product, Integer> orderProducts) {

        // Create new order with current date
        Date orderDate = Date.valueOf(LocalDate.now());
        Order order = new Order(clientId, BigDecimal.ZERO, orderDate);

        // Add products to order
        for (Map.Entry<Product, Integer> entry : orderProducts.entrySet()) {
            order.addProduct(entry.getKey(), entry.getValue());
        }

        // Calculate the amount
        order.setAmount(order.calculAmount());

        // Save to database
        boolean success = orderRepository.addOrder(order);

        return success ? order : null;
    }

    public boolean updateOrderStatus(int orderId, OrderStatus newStatus) {
        ArrayList<Order> orders = orderRepository.getAllOrders();
        for (Order order : orders) {
            if (order.getOrderId() == orderId) {
                order.setStatus(newStatus);
                return orderRepository.updateOrder(order);
            }
        }
        return false;
    }


    public boolean updateOrderProducts(int orderId, Map<Product, Integer> orderProducts) {
        ArrayList<Order> orders = orderRepository.getAllOrders();
        for (Order order : orders) {
            if (order.getOrderId() == orderId) {
                // Replace products
                order.setOrderProducts(orderProducts);
                // Recalculate amount
                order.setAmount(order.calculAmount());
                return orderRepository.updateOrder(order);
            }
        }
        return false;
    }


    public boolean cancelOrder(int orderId) {
        return updateOrderStatus(orderId, OrderStatus.CANCELLED);
    }


    public Order getOrderByClientId(int clientId) {
        return orderRepository.getOrderByClientId(clientId);
    }

    public ArrayList<Order> getAllOrders() {
        return orderRepository.getAllOrders();
    }
    public ArrayList<Order> getOrdersByStatus(OrderStatus status) {
        ArrayList<Order> allOrders = orderRepository.getAllOrders();
        ArrayList<Order> filteredOrders = new ArrayList<>();

        for (Order order : allOrders) {
            if (order.getStatus().equals(status.name())) {
                filteredOrders.add(order);
            }
        }

        return filteredOrders;
    }


    public ArrayList<Order> getOrdersByDateRange(Date startDate, Date endDate) {
        ArrayList<Order> allOrders = orderRepository.getAllOrders();
        ArrayList<Order> filteredOrders = new ArrayList<>();

        for (Order order : allOrders) {
            Date orderDate = order.getOrderDate();
            if ((orderDate.after(startDate) || orderDate.equals(startDate)) &&
                    (orderDate.before(endDate) || orderDate.equals(endDate))) {
                filteredOrders.add(order);
            }
        }

        return filteredOrders;
    }


    public Order getOrderById(int orderId) {
        return orderRepository.getOrderById(orderId);
    }

    public boolean deleteOrder(int orderId) {
        return orderRepository.deleteOrderById(orderId);
    }
}