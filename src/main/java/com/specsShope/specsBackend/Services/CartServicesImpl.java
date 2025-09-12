package com.specsShope.specsBackend.Services;

import com.specsShope.specsBackend.Dtos.CartDTO;
import com.specsShope.specsBackend.Dtos.CartItemDTO;
import com.specsShope.specsBackend.Models.Cart;
import com.specsShope.specsBackend.Models.CartItem;
import com.specsShope.specsBackend.Repository.CartRepo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
public class CartServicesImpl implements CartServices {
    @Autowired
    private CartRepo cartRepo;

    @Autowired
    private ModelMapper modelMapper;
    private CartDTO map(Cart cart) {
        return modelMapper.map(cart, CartDTO.class);
    }

    @Override
    public CartDTO getCartByUserId(String userId) {
        return map(
                cartRepo.findByUserId(userId)
                        .orElseThrow(() -> new RuntimeException("Cart not found for user: " + userId))
        );
    }

    @Override
    @Transactional
    public CartDTO addToCart(String userId, CartItemDTO dto) {
        if (dto == null || dto.getProductId() == null) {
            throw new IllegalArgumentException("Invalid cart item data");
        }
        if (dto.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be > 0");
        }
        if (dto.getPrice() == null) {
            throw new IllegalArgumentException("Price is required");
        }

        Cart cart = cartRepo.findByUserId(userId)
                .orElseGet(() -> Cart.builder().userId(userId).build());

        cart.getItems().stream()
                .filter(item -> item.getProductId().equals(dto.getProductId()))
                .findFirst()
                .ifPresentOrElse(
                        existingItem -> existingItem.setQuantity(existingItem.getQuantity() + dto.getQuantity()),
                        () -> {
                            CartItem newItem = CartItem.builder()
                                    .productId(dto.getProductId())
                                    .productName(dto.getProductName())
                                    .price(dto.getPrice())
                                    .quantity(dto.getQuantity())
                                    .imageUrl(dto.getImageUrl())
                                    .build();
                            cart.getItems().add(newItem);
                        }
                );

        updateTotalPrice(cart);
        return map(cartRepo.save(cart));
    }

    @Override
    @Transactional
    public CartDTO updateCartItem(String userId, String productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be > 0");
        }

        Cart cart = cartRepo.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found for user: " + userId));

        cart.getItems().stream()
                .filter(i -> i.getProductId().equals(productId))
                .findFirst()
                .ifPresent(item -> item.setQuantity(quantity));

        updateTotalPrice(cart);
        return map(cartRepo.save(cart));
    }

    @Override
    @Transactional
    public CartDTO removeFromCart(String userId, String productId) {
        Cart cart = cartRepo.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found for user: " + userId));

        cart.getItems().removeIf(i -> i.getProductId().equals(productId));
        updateTotalPrice(cart);
        return map(cartRepo.save(cart));
    }

    @Override
    @Transactional
    public CartDTO clearCart(String userId) {
        Cart cart = cartRepo.findByUserId(userId)
                .orElseGet(() -> Cart.builder().userId(userId).build());

        cart.getItems().clear();
        updateTotalPrice(cart);
        cartRepo.save(cart);
        return map(cart);
    }

    private void updateTotalPrice(Cart cart) {
        double total = cart.getItems().stream()
                .mapToDouble(item -> item.getPrice().doubleValue() * item.getQuantity())
                .sum();
        cart.setTotalPrice(total);
    }

}
