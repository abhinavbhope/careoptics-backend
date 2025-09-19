package com.specsShope.specsBackend.Models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EyeMeasurement {
    private Double sph;
    private Double cyl;
    private Integer axis;
    private Double add;
    private String vision; // e.g., "6/6" or "N6"
}

