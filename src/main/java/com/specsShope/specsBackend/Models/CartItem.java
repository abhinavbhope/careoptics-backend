package com.specsShope.specsBackend.Models;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem {
    private String productId;
    private String productName;
    private BigDecimal price;
    private int quantity;
    private String imageUrl;
}
