package com.specsShope.specsBackend.Models;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "doctor_past_users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorPastUser {

    @Id
    private String id;

    private String name;
    private Integer age;
    private String phone;
    private String address;

    @Builder.Default
    private List<String> doctorAppointmentIds = new ArrayList<>();

    @CreatedDate
    private LocalDateTime createdAt;
}