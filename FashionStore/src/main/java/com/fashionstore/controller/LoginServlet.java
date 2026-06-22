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

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private UserDAO userDAO = new UserDAOImpl();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        PrintWriter out = response.getWriter();

        String email    = request.getParameter("email");
        String password = request.getParameter("password");

        JsonObject jsonResponse = new JsonObject();
        User user = userDAO.loginUser(email, password);

        if (user != null) {
            HttpSession session = request.getSession();
            session.setAttribute("loggedUser", user);
            session.setAttribute("userId", user.getUserId());

            jsonResponse.addProperty("success", true);
            jsonResponse.addProperty("message", "Login successful!");
            jsonResponse.addProperty("name", user.getName());
            jsonResponse.addProperty("userId", user.getUserId());
        } else {
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Invalid email or password!");
        }

        out.print(jsonResponse);
        out.flush();
    }
}