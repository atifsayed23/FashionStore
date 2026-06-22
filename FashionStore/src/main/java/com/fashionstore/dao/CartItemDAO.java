package com.fashionstore.dao;

import java.util.List;
import com.fashionstore.model.CartItem;

public interface CartItemDAO {

    boolean addCartItem(CartItem item);

    boolean updateCartItem(CartItem item);

    boolean removeCartItem(int cartItemId);

    CartItem getCartItemById(int cartItemId);

    List<CartItem> getCartItemsByCartId(int cartId);

    boolean clearCartItems(int cartId);
}