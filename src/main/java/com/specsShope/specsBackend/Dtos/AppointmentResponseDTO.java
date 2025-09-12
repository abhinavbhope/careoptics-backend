package com.specsShope.specsBackend.Dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentResponseDTO {
    private String id;
    private String userId;
    private String name;
    private String email;
    private String phone;
    private String address;
    private List<String> eyeProblems;
    private String customProblem;
    private String preferredDate;
    private String preferredTime;
    private LocalDateTime bookedAt;
}
