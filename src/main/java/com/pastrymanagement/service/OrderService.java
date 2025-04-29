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

    /**
     * Creates a new order in the system
     *
     * @param clientId The ID of the client placing the order
     * @param orderProducts Map of products and quantities for the order
     * @return The created order or null if creation failed
     */
    public Order createOrder(int clientId, Map<Product, Integer> orderProducts) {
        // Generate a unique order ID (this could be improved)
        int orderId = (int) (System.currentTimeMillis() % Integer.MAX_VALUE);

        // Create new order with current date
        Date orderDate = Date.valueOf(LocalDate.now());
        Order order = new Order(clientId, BigDecimal.ZERO, orderDate, orderId);

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

    /**
     * Updates an existing order's status
     *
     * @param orderId The ID of the order to update
     * @param newStatus The new status to set
     * @return true if update was successful, false otherwise
     */
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

    /**
     * Updates an existing order's products and quantities
     *
     * @param orderId The ID of the order to update
     * @param orderProducts New map of products and quantities
     * @return true if update was successful, false otherwise
     */
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

    /**
     * Cancels an order
     *
     * @param orderId The ID of the order to cancel
     * @return true if cancellation was successful, false otherwise
     */
    public boolean cancelOrder(int orderId) {
        return updateOrderStatus(orderId, OrderStatus.CANCELLED);
    }

    /**
     * Gets an order by client ID
     *
     * @param clientId The client ID to search for
     * @return The order or null if not found
     */
    public Order getOrderByClientId(int clientId) {
        return orderRepository.getOrderByClientId(clientId);
    }

    /**
     * Gets all orders in the system
     *
     * @return ArrayList of all orders
     */
    public ArrayList<Order> getAllOrders() {
        return orderRepository.getAllOrders();
    }

    /**
     * Gets all orders with a specific status
     *
     * @param status The status to filter by
     * @return ArrayList of matching orders
     */
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

    /**
     * Gets orders placed between two dates
     *
     * @param startDate The start date
     * @param endDate The end date
     * @return ArrayList of matching orders
     */
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


    /**
     * Gets an order by its ID
     *
     * @param orderId The order ID to search for
     * @return The order or null if not found
     */
    public Order getOrderById(int orderId) {
        return orderRepository.getOrderById(orderId);
    }

    /**
     * Deletes an order from the system
     *
     * @param orderId The ID of the order to delete
     * @return true if deletion was successful, false otherwise
     */
    public boolean deleteOrder(int orderId) {
        return orderRepository.deleteOrderById(orderId);
    }
}