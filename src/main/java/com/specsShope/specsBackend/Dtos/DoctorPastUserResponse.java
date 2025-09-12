package com.specsShope.specsBackend.Dtos;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorPastUserResponse {

    private String id;
    private String name;
    private Integer age;
    private String phone;
    private String address;
    private List<String> doctorAppointmentIds;
    private LocalDateTime createdAt;
}