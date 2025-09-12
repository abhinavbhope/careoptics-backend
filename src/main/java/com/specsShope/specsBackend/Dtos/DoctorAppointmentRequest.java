package com.specsShope.specsBackend.Dtos;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorAppointmentRequest {

    @NotBlank
    private String patientName;

    @NotNull
    @Min(0)
    @Max(150)
    private Integer age;

    @NotBlank
    @Pattern(regexp = "\\d{10}")
    private String phone;

    @NotBlank
    private String address;

    @NotBlank
    private String reasonForVisit;


    private LocalDateTime appointmentDate;

    // nullable – for registered users we can auto-fill from token
    private String userId;
}