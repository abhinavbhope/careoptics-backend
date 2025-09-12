package com.specsShope.specsBackend.Models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "callbacks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CallbackRequest {
    @Id
    private String id;
    private String userId;          // who requested
    private String name;            // contact name
    private String phone;           // contact phone
    private String address;         // optional note
    private List<CartItem> items; // snapshot of cart
    private BigDecimal totalPrice;
    private LocalDateTime requestedAt;
    private boolean completed;      // admin sets true after call
}
