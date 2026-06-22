package com.fashionstore.controller;

import com.fashionstore.dao.OrderDAO;
import com.fashionstore.dao.OrderItemDAO;
import com.fashionstore.dao.impl.OrderDAOImpl;
import com.fashionstore.dao.impl.OrderItemDAOImpl;
import com.fashionstore.model.Order;
import com.fashionstore.model.OrderItem;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/order-confirmation")
public class OrderConfirmationServlet extends HttpServlet {

    private OrderDAO     orderDAO     = new OrderDAOImpl();
    private OrderItemDAO orderItemDAO = new OrderItemDAOImpl();
    private Gson gson                 = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String orderIdParam     = request.getParameter("orderId");
        JsonObject jsonResponse = new JsonObject();

        if (orderIdParam == null) {
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Order ID required!");
            out.print(jsonResponse);
            return;
        }

        int orderId                = Integer.parseInt(orderIdParam);
        Order order                = orderDAO.getOrderById(orderId);
        List<OrderItem> orderItems = orderDAO.getOrderItems(orderId);

        jsonResponse.addProperty("success", true);
        jsonResponse.add("order",      gson.toJsonTree(order));
        jsonResponse.add("orderItems", gson.toJsonTree(orderItems));

        out.print(jsonResponse);
        out.flush();
    }
}