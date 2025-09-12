package com.specsShope.specsBackend.Dtos;

import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record EyeTestCreatedResponse(
        String testId,
        String publicId,
        LocalDate testDate,
        EyeMeasurementDTO dvRightEye,
        EyeMeasurementDTO dvLeftEye,
        EyeMeasurementDTO nvRightEye,
        EyeMeasurementDTO nvLeftEye,
        EyeMeasurementDTO imRightEye,
        EyeMeasurementDTO imLeftEye,
        String frame,
        String lens,
        String notes,
        LocalDate bookingDate,
        LocalDate deliveryDate,
        LocalDateTime createdAt
) {}
