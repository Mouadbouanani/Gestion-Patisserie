package com.pastrymanagement.repository;

import com.pastrymanagement.model.Order;
import com.pastrymanagement.model.OrderStatus;
import com.pastrymanagement.model.Product;
import com.pastrymanagement.util.DBUtil;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Map;

public class OrderRepository {
    public boolean addOrder(Order order) {
        Connection con = DBUtil.getConnection();
        String sql = "insert into orders (order_id,order_date,amount,status,client_id) values(?,?,?,?,?)";
        try (PreparedStatement preparedStatement = con.prepareStatement(sql)){
            preparedStatement.setInt(1, order.getOrderId());
            preparedStatement.setDate(2,order.getOrderDate());
            preparedStatement.setBigDecimal(3,order.calculAmount());
            preparedStatement.setString(4,order.getStatus());
            preparedStatement.setInt(5,order.getClientId());
            return preparedStatement.executeUpdate() >0;
        }catch (SQLException x){
            x.printStackTrace();
            return false;
        }
    }
    public boolean updateOrder(Order order) {
        Connection con = DBUtil.getConnection();
        String sql ="update  orders set order_date=?,amount=?,status=?,client_id=? where order_id=? ";
        try (PreparedStatement preparedStatement = con.prepareStatement(sql)){
            preparedStatement.setDate(1,order.getOrderDate());
            preparedStatement.setBigDecimal(2,order.calculAmount());
            preparedStatement.setString(3,order.getStatus());
            preparedStatement.setInt(4,order.getClientId());
            preparedStatement.setInt(5, order.getOrderId());
            return preparedStatement.executeUpdate() >0;
        }catch (SQLException x){
            x.printStackTrace();
            return false;
        }
    }
    public boolean deleteOrder(Order order) {
        Connection con = DBUtil.getConnection();
        String sql = "delete from orders where order_id=?";
        try (PreparedStatement preparedStatement= con.prepareStatement(sql)){
            preparedStatement.setInt(1,order.getOrderId());
            return preparedStatement.executeUpdate() >0;
        }catch (SQLException x){
            x.printStackTrace();
            return false;
        }
    }
    public Order getOrderByClientId(int clientId) {
        Connection con = DBUtil.getConnection();
        String sql = "select * from orders where client_id=?";
        try(PreparedStatement preparedStatement=con.prepareStatement(sql)){
            preparedStatement.setInt(1,clientId);
            ResultSet rs=preparedStatement.executeQuery();
            if (rs.next()) {
                Order order = new Order(
                        clientId,
                        rs.getBigDecimal("amount"),
                        rs.getDate("order_date"),
                        OrderStatus.valueOf(rs.getString("status")) // Correct conversion
                );
                return order;
            }else{
                System.out.println("Order not found");
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    public ArrayList<Order> getAllOrders() {
        Connection con = DBUtil.getConnection();
        String sql = "select * from orders";
        ArrayList<Order> orderList = new ArrayList<>();

        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int orderId = rs.getInt("order_id");
                Date orderDate = rs.getDate("order_date");
                BigDecimal amount = rs.getBigDecimal("amount");
                String statusStr = rs.getString("status");
                int clientId = rs.getInt("client_id");

                try {
                    OrderStatus status = OrderStatus.valueOf(statusStr);
                    Order order = new Order(clientId, amount, orderDate, orderId);
                    order.setStatus(status);
                    orderList.add(order);
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid order status found for order " + orderId + ": " + statusStr);
                }
            }

        } catch (SQLException x) {
            x.printStackTrace();
        }

        return orderList;
    }
    public Order getOrderById(int id) {
        Connection con = DBUtil.getConnection();
        String sql = "SELECT * FROM orders WHERE order_id = ?";

        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int clientId = rs.getInt("client_id");
                BigDecimal amount = rs.getBigDecimal("amount");
                Date orderDate = rs.getDate("order_date");
                String statusStr = rs.getString("status");

                OrderStatus status = OrderStatus.valueOf(statusStr);
                Order order = new Order(clientId, amount, orderDate, id);
                order.setStatus(status);
                return order;
            } else {
                System.out.println("No order found with ID: " + id);
                return null;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    public boolean deleteOrderById(int orderId) {
        Connection con = DBUtil.getConnection();
        String sql = "DELETE FROM orders WHERE order_id = ?";

        try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            preparedStatement.setInt(1, orderId);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }



}