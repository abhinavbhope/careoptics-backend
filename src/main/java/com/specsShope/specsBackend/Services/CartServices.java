package com.specsShope.specsBackend.Services;

import com.specsShope.specsBackend.Dtos.CartDTO;
import com.specsShope.specsBackend.Dtos.CartItemDTO;

public interface CartServices {
    CartDTO getCartByUserId(String userId);
    CartDTO addToCart(String userId, CartItemDTO cartItemDTO);
    CartDTO updateCartItem(String userId, String productId, int quantity);
    CartDTO removeFromCart(String userId, String productId);
    CartDTO clearCart(String userId);
}
