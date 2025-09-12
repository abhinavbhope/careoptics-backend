package com.specsShope.specsBackend.Dtos;

import jakarta.validation.constraints.*;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ReviewRequestDTO {
    @NotBlank private String productId;
    @Min(1) @Max(5) private int rating;
    @Size(max = 1000) private String comment;

}