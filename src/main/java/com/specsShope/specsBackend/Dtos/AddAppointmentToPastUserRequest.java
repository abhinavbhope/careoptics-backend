package com.specsShope.specsBackend.Dtos;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddAppointmentToPastUserRequest {

    @NotBlank
    private String reasonForVisit;

    private LocalDateTime appointmentDate;
}