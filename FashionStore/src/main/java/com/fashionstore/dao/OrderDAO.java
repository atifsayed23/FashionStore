package com.fashionstore.dao;

import java.util.List;
import com.fashionstore.model.Order;
import com.fashionstore.model.OrderItem;

public interface OrderDAO {

    int placeOrder(Order order, List<OrderItem> items);

    List<Order> getOrdersByUserId(int userId);

    Order getOrderById(int orderId);

    boolean updateOrderStatus(int orderId, String status);

    boolean cancelOrder(int orderId);

    List<OrderItem> getOrderItems(int orderId);
}