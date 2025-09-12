package com.specsShope.specsBackend.Dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExternalEyeTestFormDTO {

    /* === Personal fields for on-the-fly user creation === */
    @NotBlank private String name;
    @Pattern(regexp = "^[0-9]{10}$") private String phone;
    @NotBlank private Integer age;
    @Email @NotBlank private String email;
    @NotBlank private String address;

    /* === Same measurement block reused === */
    @Valid @NotNull private EyeMeasurementDTO dvRightEye;
    @Valid @NotNull private EyeMeasurementDTO dvLeftEye;
    @Valid @NotNull private EyeMeasurementDTO nvRightEye;
    @Valid @NotNull private EyeMeasurementDTO nvLeftEye;
    @Valid private EyeMeasurementDTO imRightEye;
    @Valid private EyeMeasurementDTO imLeftEye;

    private String frame;
    private String lens;
    @Size(max = 500) private String notes;
    private LocalDate bookingDate;
    private LocalDate deliveryDate;
}