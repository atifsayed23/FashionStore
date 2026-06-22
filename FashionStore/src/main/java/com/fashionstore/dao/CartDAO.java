package com.fashionstore.dao;

import com.fashionstore.model.Cart;
import java.util.List;

public interface CartDAO {
    boolean addToCart(Cart cart);
    List<Cart> getCartByUserId(int userId);
    boolean updateCartQuantity(int cartId, int quantity);
    boolean removeFromCart(int cartId);
    boolean clearCart(int userId);
    boolean isVariantInCart(int userId, int variantId);
    int getCartItemCount(int userId);
}