package com.fashionstore.controller;

import com.fashionstore.dao.UserDAO;
import com.fashionstore.dao.impl.UserDAOImpl;
import com.fashionstore.model.User;
import com.google.gson.JsonObject;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private UserDAO userDAO = new UserDAOImpl();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        PrintWriter out = response.getWriter();

        String name     = request.getParameter("name");
        String email    = request.getParameter("email");
        String password = request.getParameter("password");
        String phone    = request.getParameter("phone");
        String address  = request.getParameter("address");

        JsonObject jsonResponse = new JsonObject();

        if (userDAO.isEmailExists(email)) {
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Email already registered!");
            out.print(jsonResponse);
            return;
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setPhone(phone);
        user.setAddress(address);

        boolean registered = userDAO.registerUser(user);

        if (registered) {
            jsonResponse.addProperty("success", true);
            jsonResponse.addProperty("message", "Registration successful!");
        } else {
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Registration failed. Try again!");
        }

        out.print(jsonResponse);
        out.flush();
    }
}