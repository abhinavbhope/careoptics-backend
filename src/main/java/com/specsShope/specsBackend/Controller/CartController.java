package com.specsShope.specsBackend.Controller;

import com.specsShope.specsBackend.Dtos.CartDTO;
import com.specsShope.specsBackend.Dtos.CartItemDTO;
import com.specsShope.specsBackend.Models.CustomUserDetails;
import com.specsShope.specsBackend.Services.CartServices;
import com.specsShope.specsBackend.Services.JwtService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/cart")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
@Validated
public class CartController {

    private final CartServices cartService;
    private final JwtService jwtService;


    @GetMapping("/me")
    public ResponseEntity<CartDTO> getMyCart(
            @AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(cartService.getCartByUserId(user.getUser().getId().toString()));
    }

    @PostMapping("/me/items")
    public ResponseEntity<CartDTO> addToMyCart(
            @AuthenticationPrincipal CustomUserDetails user,
            @Valid @RequestBody CartItemDTO dto) {
        return ResponseEntity.ok(cartService.addToCart(user.getUser().getId().toString(), dto));
    }

    @PutMapping("/me/items/{productId}")
    public ResponseEntity<CartDTO> updateMyCartItem(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable String productId,
            @RequestParam int quantity) {
        return ResponseEntity.ok(
                cartService.updateCartItem(user.getUser().getId().toString(), productId, quantity));
    }

    @DeleteMapping("/me/items/{productId}")
    public ResponseEntity<CartDTO> removeFromMyCart(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable String productId) {
        return ResponseEntity.ok(
                cartService.removeFromCart(user.getUser().getId().toString(), productId));
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> clearMyCart(
            @AuthenticationPrincipal CustomUserDetails user) {
        cartService.clearCart(user.getUser().getId().toString());
        return ResponseEntity.noContent().build();
    }
}