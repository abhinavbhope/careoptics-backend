package com.specsShope.specsBackend.Models;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDateTime;

@Document(collection = "reviews")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {
    @Id
    private String id;

    private String userId;     // Reviewer
    private String productId;  // Reviewed product
    private String username;
    private int rating;        // 1 to 5
    private String comment;

    @CreatedDate
    private Instant createdAt;
}
