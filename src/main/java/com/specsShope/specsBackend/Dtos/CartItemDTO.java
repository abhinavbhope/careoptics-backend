package com.specsShope.specsBackend.Dtos;
import java.math.BigDecimal;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemDTO {
    private String productId;
    private String productName;
    private BigDecimal price;
    private int quantity;
    private String imageUrl;
}
