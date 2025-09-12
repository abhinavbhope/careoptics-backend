package com.specsShope.specsBackend.Dtos;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CallbackResponseDTO {
    private String id;
    private String userId;
    private String name;
    private String phone;
    private String address;
    private List<CartItemDTO> items;   // reuse CartItemDTO or inline fields
    private BigDecimal totalPrice;
    private LocalDateTime requestedAt;
    private boolean completed;
}
