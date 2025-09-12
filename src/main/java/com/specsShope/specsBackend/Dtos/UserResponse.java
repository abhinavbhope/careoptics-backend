package com.specsShope.specsBackend.Dtos;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private String id;
    private String name;
    private String email;
    private String phone;
    private String role;
    private List<DoctorAppointmentResponse> appointments;
}
