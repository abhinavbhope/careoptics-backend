package com.specsShope.specsBackend.Models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    private String id;
    private String name;

    @Indexed(unique = true)
    private String email;
    private String address;
    @Indexed(unique = true)
    private String username;
    private Integer age;
    private String password;
    private Role role; // "USER" or "ADMIN"
    private String phone;
    private List<String> appointments; // List of Appointment IDs
    private String otp;
    private LocalDateTime otpExpiry;
    @CreatedDate
    private LocalDateTime createdAt;
    private boolean external;
    @Builder.Default
    private List<EyeTestRecord> eyeTestHistory = new ArrayList<>();
    @Builder.Default
    private List<String> doctorAppointmentIds = new ArrayList<>();
}
