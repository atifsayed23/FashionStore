package com.fashionstore.controller;

import com.fashionstore.dao.OrderDAO;
import com.fashionstore.dao.impl.OrderDAOImpl;
import com.fashionstore.model.Order;
import com.fashionstore.model.User;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/my-orders")
public class MyOrdersServlet extends HttpServlet {

    private OrderDAO orderDAO = new OrderDAOImpl();
    private Gson gson         = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        HttpSession session     = request.getSession(false);
        JsonObject jsonResponse = new JsonObject();

        if (session == null || session.getAttribute("loggedUser") == null) {
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Please login first!");
            out.print(jsonResponse);
            return;
        }

        User loggedUser     = (User) session.getAttribute("loggedUser");
        List<Order> orders  = orderDAO.getOrdersByUserId(loggedUser.getUserId());

        jsonResponse.addProperty("success", true);
        jsonResponse.add("orders", gson.toJsonTree(orders));

        out.print(jsonResponse);
        out.flush();
    }
}