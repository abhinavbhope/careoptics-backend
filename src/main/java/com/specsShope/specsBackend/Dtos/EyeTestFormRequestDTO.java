package com.specsShope.specsBackend.Dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EyeTestFormRequestDTO {

    private Integer age;       // ✅ allow updating user age
    private String name;       // optional
    private String phone;      // optional
    private String address;    // optional

    /* === Eye-test measurements === */
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
    @Builder.Default
    private LocalDate testDate = LocalDate.now();
}