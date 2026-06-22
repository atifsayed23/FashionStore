package com.fashionstore.controller;

import com.fashionstore.dao.CartDAO;
import com.fashionstore.dao.impl.CartDAOImpl;
import com.fashionstore.model.Cart;
import com.fashionstore.model.User;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/cart")
public class CartServlet extends HttpServlet {

    private CartDAO cartDAO = new CartDAOImpl();
    private Gson gson       = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(false);
        JsonObject jsonResponse = new JsonObject();

        if (session == null || session.getAttribute("loggedUser") == null) {
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Not logged in");
            out.print(jsonResponse);
            return;
        }

        User loggedUser      = (User) session.getAttribute("loggedUser");
        List<Cart> cartItems = cartDAO.getCartByUserId(loggedUser.getUserId());
        int itemCount        = cartDAO.getCartItemCount(loggedUser.getUserId());

        jsonResponse.addProperty("success", true);
        jsonResponse.add("cartItems", gson.toJsonTree(cartItems));
        jsonResponse.addProperty("itemCount", itemCount);

        out.print(jsonResponse);
        out.flush();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
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

        User loggedUser = (User) session.getAttribute("loggedUser");
        int variantId   = Integer.parseInt(request.getParameter("variantId"));
        int quantity    = Integer.parseInt(request.getParameter("quantity"));

        if (cartDAO.isVariantInCart(loggedUser.getUserId(), variantId)) {
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Item already in cart!");
            out.print(jsonResponse);
            return;
        }

        Cart cart = new Cart();
        cart.setUserId(loggedUser.getUserId());
        cart.setVariantId(variantId);
        cart.setQuantity(quantity);

        boolean added = cartDAO.addToCart(cart);
        jsonResponse.addProperty("success", added);
        jsonResponse.addProperty("message", added ? "Item added to cart!" : "Failed to add!");

        out.print(jsonResponse);
        out.flush();
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        int cartId   = Integer.parseInt(request.getParameter("cartId"));
        int quantity = Integer.parseInt(request.getParameter("quantity"));

        boolean updated         = cartDAO.updateCartQuantity(cartId, quantity);
        JsonObject jsonResponse = new JsonObject();
        jsonResponse.addProperty("success", updated);
        jsonResponse.addProperty("message", updated ? "Cart updated!" : "Failed!");

        out.print(jsonResponse);
        out.flush();
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        int cartId              = Integer.parseInt(request.getParameter("cartId"));
        boolean removed         = cartDAO.removeFromCart(cartId);
        JsonObject jsonResponse = new JsonObject();
        jsonResponse.addProperty("success", removed);
        jsonResponse.addProperty("message", removed ? "Item removed!" : "Failed!");

        out.print(jsonResponse);
        out.flush();
    }
}