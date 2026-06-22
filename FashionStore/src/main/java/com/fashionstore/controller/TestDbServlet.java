package com.fashionstore.controller;

import java.io.IOException;
import java.sql.Connection;

import com.fashionstore.util.DBConnection;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/test-db")
public class TestDbServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Connection conn = DBConnection.getConnection();

        if (conn != null) {
            resp.getWriter().println("DB Connected Successfully!");
            DBConnection.closeConnection(conn);
        } else {
            resp.getWriter().println("DB Connection Failed!");
        }
    }
}