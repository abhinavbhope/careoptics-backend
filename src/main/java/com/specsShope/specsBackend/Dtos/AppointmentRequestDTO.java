package com.specsShope.specsBackend.Dtos;

import lombok.Data;

import java.util.List;

@Data
public class AppointmentRequestDTO {
    private String userId;
    private String name;
    private String email;
    private String phone;
    private String address;
    private List<String> eyeProblems;
    private String customProblem;
    private String preferredDate;
    private String preferredTime;
}

