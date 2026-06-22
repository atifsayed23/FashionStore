package com.fashionstore.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import com.fashionstore.dao.CartDAO;
import com.fashionstore.dao.OrderDAO;
import com.fashionstore.dao.OrderItemDAO;
import com.fashionstore.dao.ProductDAO;
import com.fashionstore.dao.ProductVariantDAO;
import com.fashionstore.dao.impl.CartDAOImpl;
import com.fashionstore.dao.impl.OrderDAOImpl;
import com.fashionstore.dao.impl.OrderItemDAOImpl;
import com.fashionstore.dao.impl.ProductDAOImpl;
import com.fashionstore.dao.impl.ProductVariantDAOImpl;
import com.fashionstore.model.Cart;
import com.fashionstore.model.Order;
import com.fashionstore.model.OrderItem;
import com.fashionstore.model.Product;
import com.fashionstore.model.ProductVariant;
import com.fashionstore.model.User;
import com.google.gson.JsonObject;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/checkout")
public class CheckoutServlet extends HttpServlet {

    private CartDAO           cartDAO           = new CartDAOImpl();
    private OrderDAO          orderDAO          = new OrderDAOImpl();
    private OrderItemDAO      orderItemDAO      = new OrderItemDAOImpl();
    private ProductVariantDAO productVariantDAO = new ProductVariantDAOImpl();
    private ProductDAO        productDAO        = new ProductDAOImpl();

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

        User loggedUser          = (User) session.getAttribute("loggedUser");
        List<Cart> cartItems     = cartDAO.getCartByUserId(loggedUser.getUserId());

        if (cartItems.isEmpty()) {
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Cart is empty!");
            out.print(jsonResponse);
            return;
        }

        // Calculate total
        double totalAmount = 0;
        for (Cart cartItem : cartItems) {
            ProductVariant variant = productVariantDAO.getVariantById(cartItem.getVariantId());
            Product product        = productDAO.getProductById(variant.getProductId());
            double itemPrice       = product.getPrice() + variant.getExtraPrice();
            totalAmount           += itemPrice * cartItem.getQuantity();
        }

        // Place order
        Order order = new Order();
        order.setUserId(loggedUser.getUserId());
        order.setTotalAmount(totalAmount);
        order.setStatus("Pending");

        // Build items list
        List<OrderItem> itemsToOrder = new java.util.ArrayList<>();
        for (Cart cartItem : cartItems) {
            ProductVariant variant = productVariantDAO.getVariantById(cartItem.getVariantId());
            Product product        = productDAO.getProductById(variant.getProductId());
            double itemPrice       = product.getPrice(); // variant.getExtraPrice() is not needed since we removed it

            OrderItem orderItem = new OrderItem();
            orderItem.setVariantId(cartItem.getVariantId());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(itemPrice);
            itemsToOrder.add(orderItem);
        }

        int orderId = orderDAO.placeOrder(order, itemsToOrder);

        if (orderId == -1) {
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Failed to place order!");
            out.print(jsonResponse);
            return;
        }

        // Update stock
        for (Cart cartItem : cartItems) {
            productVariantDAO.updateVariantStock(cartItem.getVariantId(), cartItem.getQuantity());
        }

        // Clear cart
        cartDAO.clearCart(loggedUser.getUserId());

        jsonResponse.addProperty("success", true);
        jsonResponse.addProperty("message", "Order placed successfully!");
        jsonResponse.addProperty("orderId", orderId);

        out.print(jsonResponse);
        out.flush();
    }
}
