package com.fashionstore.controller;

import com.fashionstore.dao.CategoryDAO;
import com.fashionstore.dao.ProductDAO;
import com.fashionstore.dao.impl.CategoryDAOImpl;
import com.fashionstore.dao.impl.ProductDAOImpl;
import com.fashionstore.model.Category;
import com.fashionstore.model.Product;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/home")
public class HomeServlet extends HttpServlet {

    private ProductDAO  productDAO  = new ProductDAOImpl();
    private CategoryDAO categoryDAO = new CategoryDAOImpl();
    private Gson gson               = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        PrintWriter out = response.getWriter();

        JsonObject jsonResponse = new JsonObject();

        try {
            List<Product>  featuredProducts = productDAO.getFeaturedProducts(8);
            List<Category> categories       = categoryDAO.getAllCategories();

            if (featuredProducts == null) featuredProducts = new ArrayList<>();
            if (categories == null)       categories       = new ArrayList<>();

            System.out.println("Products count: " + featuredProducts.size());
            System.out.println("Categories count: " + categories.size());

            jsonResponse.addProperty("success", true);
            jsonResponse.add("featuredProducts", gson.toJsonTree(featuredProducts));
            jsonResponse.add("categories",       gson.toJsonTree(categories));

        } catch (Exception e) {
            System.out.println("HomeServlet Error: " + e.getMessage());
            e.printStackTrace();
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", e.getMessage());
        }

        out.print(jsonResponse);
        out.flush();
    }
}