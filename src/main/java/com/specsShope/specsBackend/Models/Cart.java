package com.specsShope.specsBackend.Models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "carts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cart {
    @Id
    private String id;

    private String userId; // Reference to User

    @Builder.Default
    private List<CartItem> items = new ArrayList<>();
    private double totalPrice; // Optional: can calculate dynamically
}
