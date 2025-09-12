package com.specsShope.specsBackend.Dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminReviewUpdateDTO {
    private String productId; // optional for admin
    @Min(1) @Max(5)
    private int rating;
    @Size(max = 1000)
    private String comment;
}
