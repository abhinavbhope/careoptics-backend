package com.specsShope.specsBackend.Services;

import com.specsShope.specsBackend.Dtos.AppointmentReasonStatsDTO;
import com.specsShope.specsBackend.Dtos.AppointmentRequestDTO;
import com.specsShope.specsBackend.Dtos.AppointmentResponseDTO;
import com.specsShope.specsBackend.Models.Appointment;
import com.specsShope.specsBackend.Repository.AppointmentReasonProjection;

import java.util.List;
import java.util.Map;

public interface AppointmentService {
    AppointmentResponseDTO bookAppointment(AppointmentRequestDTO requestDTO);
    List<AppointmentResponseDTO> getAllAppointments();
    List<AppointmentResponseDTO> getUserAppointments(String userId);
    List<String> getAvailableSlots(String preferredDate);
    List<Map<String, Object>> getAppointmentsSummary();
    List<AppointmentResponseDTO> getAppointmentsByDate(String date);
    List<AppointmentResponseDTO> getRecentAppointments(int limit);
    List<AppointmentReasonStatsDTO> countAppointmentsByReason();


}