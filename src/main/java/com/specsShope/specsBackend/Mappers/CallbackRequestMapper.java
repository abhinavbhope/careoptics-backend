
package com.specsShope.specsBackend.Mappers;

import com.specsShope.specsBackend.Dtos.CallbackResponseDTO;
import com.specsShope.specsBackend.Dtos.CartItemDTO;
import com.specsShope.specsBackend.Models.CallbackRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CallbackRequestMapper {

    public CallbackResponseDTO toDto(CallbackRequest callback) {
        List<CartItemDTO> itemDTOs = callback.getItems().stream()
                .map(item -> CartItemDTO.builder()
                        .productId(item.getProductId())
                        .productName(item.getProductName())
                        .price(item.getPrice())
                        .quantity(item.getQuantity())
                        .imageUrl(item.getImageUrl())
                        .build())
                .collect(Collectors.toList());

        return CallbackResponseDTO.builder()
                .id(callback.getId())
                .userId(callback.getUserId())
                .name(callback.getName())
                .phone(callback.getPhone())
                .address(callback.getAddress())
                .items(itemDTOs)
                .totalPrice(callback.getTotalPrice())
                .requestedAt(callback.getRequestedAt())
                .completed(callback.isCompleted())
                .build();
    }
}