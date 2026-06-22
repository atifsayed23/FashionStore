package com.fashionstore.controller;

import com.fashionstore.dao.ProductDAO;
import com.fashionstore.dao.ProductVariantDAO;
import com.fashionstore.dao.impl.ProductDAOImpl;
import com.fashionstore.dao.impl.ProductVariantDAOImpl;
import com.fashionstore.model.Product;
import com.fashionstore.model.ProductVariant;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/product-detail")
public class ProductDetailServlet extends HttpServlet {

    private ProductDAO        productDAO        = new ProductDAOImpl();
    private ProductVariantDAO productVariantDAO = new ProductVariantDAOImpl();
    private Gson gson                           = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        PrintWriter out = response.getWriter();

        String productIdParam   = request.getParameter("productId");
        JsonObject jsonResponse = new JsonObject();

        if (productIdParam == null) {
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Product ID required!");
            out.print(jsonResponse);
            return;
        }

        int productId                    = Integer.parseInt(productIdParam);
        Product product                  = productDAO.getProductById(productId);
        List<ProductVariant> variants    = productVariantDAO.getVariantsByProductId(productId);
        List<String> sizes               = productVariantDAO.getAvailableSizes(productId);
        List<String> colors              = productVariantDAO.getAvailableColors(productId);

        jsonResponse.addProperty("success", true);
        jsonResponse.add("product",  gson.toJsonTree(product));
        jsonResponse.add("variants", gson.toJsonTree(variants));
        jsonResponse.add("sizes",    gson.toJsonTree(sizes));
        jsonResponse.add("colors",   gson.toJsonTree(colors));

        out.print(jsonResponse);
        out.flush();
    }
}