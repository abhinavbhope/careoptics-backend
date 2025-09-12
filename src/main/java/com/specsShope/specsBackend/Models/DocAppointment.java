package com.specsShope.specsBackend.Models;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "docappointments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocAppointment {
    @Id
    private String id;

    private String patientName;
    private Integer age;
    private String phone;
    private String address;
    private String reasonForVisit;
    private LocalDateTime appointmentDate;

    @CreatedDate
    private LocalDateTime createdAt;

    // For registered users
    private String userId;
    private String pastUserId; // Nullable for walk-ins
}