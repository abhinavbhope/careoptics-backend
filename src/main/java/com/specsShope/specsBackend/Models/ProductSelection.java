package com.specsShope.specsBackend.Models;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductSelection {
    private String productId;
    private int quantity;
}
