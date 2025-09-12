package com.specsShope.specsBackend.Dtos;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorAppointmentResponse {

    private String id;
    private String patientName;
    private Integer age;
    private String phone;
    private String address;
    private String reasonForVisit;
    private LocalDateTime appointmentDate;
    private LocalDateTime createdAt;
    private String userId; // null == walk-in
}