package com.specsShope.specsBackend.Dtos;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EyeMeasurementDTO {

    // Typical range (-30 to +30), adjust to your business limits

    private Double sph;



    private Double cyl;



    private Integer axis;

    // optional for bifocal/reading

    private Double add;


    private String vision;
}
