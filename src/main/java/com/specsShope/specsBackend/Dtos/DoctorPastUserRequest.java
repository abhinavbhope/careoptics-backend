package com.specsShope.specsBackend.Dtos;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorPastUserRequest {

    @NotBlank
    private String name;

    @NotNull
    @Min(0) @Max(150)
    private Integer age;

    @NotBlank
    @Pattern(regexp = "\\d{10}")
    private String phone;

    @NotBlank
    private String address;
}