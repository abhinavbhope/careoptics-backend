package com.specsShope.specsBackend.Mappers;

import com.specsShope.specsBackend.Dtos.AppointmentResponseDTO;
import com.specsShope.specsBackend.Models.Appointment;
import org.springframework.stereotype.Component;

@Component
public class AppointmentMapper {

    public AppointmentResponseDTO toDto(Appointment appointment) {
        if (appointment == null) {
            return null;
        }

        return AppointmentResponseDTO.builder()
                .id(appointment.getId())
                .userId(appointment.getUserId())
                .name(appointment.getName())
                .email(appointment.getEmail())
                .phone(appointment.getPhone())
                .address(appointment.getAddress())
                .eyeProblems(appointment.getEyeProblems())
                .customProblem(appointment.getCustomProblem())
                .preferredDate(appointment.getPreferredDate())
                .preferredTime(appointment.getPreferredTime())
                .bookedAt(appointment.getBookedAt())
                .build();
    }
}