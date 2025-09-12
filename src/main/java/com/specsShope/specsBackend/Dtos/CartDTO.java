package com.specsShope.specsBackend.Dtos;

import java.util.List;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartDTO {
    private String id;
    private String userId;
    private List<CartItemDTO> items;
    private double totalPrice;
}
