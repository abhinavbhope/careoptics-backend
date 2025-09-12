package com.specsShope.specsBackend.Dtos;

import lombok.*;

import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ReviewResponseDTO {
    private String id;
    private String userId;
    private String productId;
    private String productName;
    private String username;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;
}