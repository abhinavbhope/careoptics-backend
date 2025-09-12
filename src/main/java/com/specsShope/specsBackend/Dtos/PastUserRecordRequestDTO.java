package com.specsShope.specsBackend.Dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PastUserRecordRequestDTO {
    @NotBlank
    private String name;
    @Min(1) private int age;
    @NotBlank private String phone;
    private String email;
    private String address;
}
