package com.fashionstore.controller;

import com.fashionstore.dao.ProductDAO;
import com.fashionstore.dao.impl.ProductDAOImpl;
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
import java.util.List;

@WebServlet("/products")
public class ProductListServlet extends HttpServlet {

    private ProductDAO productDAO = new ProductDAOImpl();
    private Gson gson             = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        PrintWriter out = response.getWriter();

        String categoryId = request.getParameter("categoryId");
        String keyword    = request.getParameter("keyword");
        String minPrice   = request.getParameter("minPrice");
        String maxPrice   = request.getParameter("maxPrice");

        List<Product> products;
        JsonObject jsonResponse = new JsonObject();

        try {
            if (keyword != null && !keyword.isEmpty()) {
                products = productDAO.searchProducts(keyword);
            } else if (categoryId != null && !categoryId.isEmpty()
                    && minPrice != null && maxPrice != null) {
                products = productDAO.filterByCategoryAndPrice(
                        Integer.parseInt(categoryId),
                        Double.parseDouble(minPrice),
                        Double.parseDouble(maxPrice));
            } else if (categoryId != null && !categoryId.isEmpty()) {
                products = productDAO.getProductsByCategory(Integer.parseInt(categoryId));
            } else if (minPrice != null && maxPrice != null) {
                products = productDAO.filterByPrice(
                        Double.parseDouble(minPrice),
                        Double.parseDouble(maxPrice));
            } else {
                products = productDAO.getAllProducts();
            }

            System.out.println("Products returned: " + products.size());
            jsonResponse.addProperty("success", true);
            jsonResponse.add("products", gson.toJsonTree(products));

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", e.getMessage());
        }

        out.print(jsonResponse);
        out.flush();
    }
}