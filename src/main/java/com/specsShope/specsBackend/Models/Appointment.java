package com.specsShope.specsBackend.Models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "appointments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {
    @Id
    private String id;
    private String userId; // To track which user booked the appointment
    private String name;
    private String email;
    private String phone;
    private String address;
    private List<String> eyeProblems;// Combined problems field
    private String customProblem;
    @Field("preferredDate")// For "Other" problems
    private String preferredDate;
    @Field("preferredTime")
    private String preferredTime;
    private LocalDateTime bookedAt;
}