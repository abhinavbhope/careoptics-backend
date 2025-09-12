package com.specsShope.specsBackend.Models;
import lombok.*;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EyeTestRecord {
    @Id
    private String testId;
    private String userId;
    private LocalDate testDate;

    // Distant Vision
    private EyeMeasurement dvRightEye;
    private EyeMeasurement dvLeftEye;

    // Near Vision
    private EyeMeasurement nvRightEye;
    private EyeMeasurement nvLeftEye;

    // Intermediate
    private EyeMeasurement imRightEye;
    private EyeMeasurement imLeftEye;

    // Frame/Lens/Notes/Booking/Delivery from form
    private String frame;
    private String lens;
    private String notes; // Multiple notes possible
    private LocalDate bookingDate;
    private LocalDate deliveryDate;
    private LocalDateTime createdAt;
}
