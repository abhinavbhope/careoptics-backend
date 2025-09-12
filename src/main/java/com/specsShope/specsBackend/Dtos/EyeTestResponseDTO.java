package com.specsShope.specsBackend.Dtos;

import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class EyeTestResponseDTO {
    private String testId;
    private String userId;
    private LocalDate testDate;

    /* Personal fields returned for completeness */
    private String name;
    private String phone;
    private String email;
    private String address;
    private Integer age;

    /* Measurements */
    private EyeMeasurementDTO dvRightEye;
    private EyeMeasurementDTO dvLeftEye;
    private EyeMeasurementDTO nvRightEye;
    private EyeMeasurementDTO nvLeftEye;
    private EyeMeasurementDTO imRightEye;
    private EyeMeasurementDTO imLeftEye;

    private String frame;
    private String lens;
    private String notes;          // <= single string
    private LocalDate bookingDate;
    private LocalDate deliveryDate;
}