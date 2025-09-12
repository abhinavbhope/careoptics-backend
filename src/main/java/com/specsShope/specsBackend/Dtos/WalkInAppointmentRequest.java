package com.specsShope.specsBackend.Dtos;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WalkInAppointmentRequest {

    @NotBlank(message = "Patient name is required")
    private String patientName;

    @NotNull
    @Min(0) @Max(150)
    private Integer age;

    @NotBlank
    @Pattern(regexp = "\\d{10}", message = "10-digit phone number required")
    private String phone;

    @NotBlank
    private String address;

    @NotBlank
    private String reasonForVisit;

    @Future(message = "Appointment date must be in the future")
    private LocalDateTime appointmentDate;
}