package com.specsShope.specsBackend.Dtos;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EyeMeasurementDTO {

    // Typical range (-30 to +30), adjust to your business limits
    @NotNull(message = "SPH value is required")
    @DecimalMin(value = "-30.0", message = "SPH too low")
    @DecimalMax(value = "30.0",  message = "SPH too high")
    private Double sph;


    @DecimalMin(value = "-12.0", message = "CYL too low")
    @DecimalMax(value = "12.0",  message = "CYL too high")
    private Double cyl;


    @Min(value = 0,   message = "Axis must be >= 0")
    @Max(value = 180, message = "Axis must be <= 180")
    private Integer axis;

    // optional for bifocal/reading
    @DecimalMin(value = "0.0", message = "ADD cannot be negative")
    @DecimalMax(value = "4.0", message = "ADD too high")
    private Double add;

    // optional visual acuity string e.g., "6/6", "N6"
    @Size(max = 10, message = "Vision text too long")
    private String vision;
}
