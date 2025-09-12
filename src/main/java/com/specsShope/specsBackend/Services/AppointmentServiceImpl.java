package com.specsShope.specsBackend.Services;

import com.specsShope.specsBackend.Dtos.AppointmentReasonStatsDTO;
import com.specsShope.specsBackend.Dtos.AppointmentRequestDTO;
import com.specsShope.specsBackend.Dtos.AppointmentResponseDTO;
import com.specsShope.specsBackend.Mappers.AppointmentMapper;
import com.specsShope.specsBackend.Models.Appointment;
import com.specsShope.specsBackend.Repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentMapper appointmentMapper;
    private final EmailService emailService;
    private final String adminEmail;

    // Constructor injection - consistent with EmailServiceImpl
    public AppointmentServiceImpl(AppointmentRepository appointmentRepository,
                                  AppointmentMapper appointmentMapper,
                                  EmailService emailService,
                                  @Value("${admin.email:abhinavdecide@gmail.com}") String adminEmail) {
        this.appointmentRepository = appointmentRepository;
        this.appointmentMapper = appointmentMapper;
        this.emailService = emailService;
        this.adminEmail = adminEmail;
    }

    @Override
    public AppointmentResponseDTO bookAppointment(AppointmentRequestDTO requestDTO) {
        // ✅ Check for existing booking at the same date and time
        boolean isSlotTaken = appointmentRepository
                .existsByPreferredDateAndPreferredTime(requestDTO.getPreferredDate(), requestDTO.getPreferredTime());

        if (isSlotTaken) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "This time slot is already booked. Please select another time.");
        }

        // 👇 Rest of your existing code
        Appointment appointment = Appointment.builder()
                .userId(requestDTO.getUserId())
                .name(requestDTO.getName())
                .email(requestDTO.getEmail())
                .phone(requestDTO.getPhone())
                .address(requestDTO.getAddress())
                .eyeProblems(requestDTO.getEyeProblems())
                .customProblem(requestDTO.getCustomProblem())
                .preferredDate(requestDTO.getPreferredDate())   // <- ADD THIS
                .preferredTime(requestDTO.getPreferredTime()
                        .trim()
                        .toUpperCase())        // <- KEEP ONLY THIS ONE
                .bookedAt(LocalDateTime.now())
                .build();

        Appointment savedAppointment = appointmentRepository.save(appointment);

        try {
            sendAppointmentConfirmationToUser(savedAppointment.getEmail(), savedAppointment.getName(),
                    savedAppointment.getPreferredDate(), savedAppointment.getPreferredTime());

            sendAppointmentNotificationToAdmin(adminEmail, savedAppointment.getName(),
                    savedAppointment.getEmail(), savedAppointment.getPhone(),
                    savedAppointment.getPreferredDate(), savedAppointment.getPreferredTime(),
                    savedAppointment.getEyeProblems() + (
                            savedAppointment.getCustomProblem() != null ?
                                    ", " + savedAppointment.getCustomProblem() : "")
            );
        } catch (Exception e) {
            System.err.println("Failed to send email notifications: " + e.getMessage());
        }

        return appointmentMapper.toDto(savedAppointment);
    }


    @Override
    public List<AppointmentResponseDTO> getAllAppointments() {
        return appointmentRepository.findAll().stream()
                .map(appointmentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentResponseDTO> getUserAppointments(String userId) {
        return appointmentRepository.findByUserId(userId).stream()
                .map(appointmentMapper::toDto)
                .collect(Collectors.toList());
    }

    // Private methods for sending specific appointment emails
    private void sendAppointmentConfirmationToUser(String userEmail, String userName,
                                                   String appointmentDate, String appointmentTime) {
        String subject = "Appointment Booked Successfully - CareOptics";

        String body = """
        <html>
        <head>
            <style>
                body { font-family: Arial, sans-serif; background-color: #f4f4f9; color: #333; }
                .container { max-width: 600px; margin: 20px auto; background-color: #fff; padding: 20px; border-radius: 10px; box-shadow: 0 4px 6px rgba(0,0,0,0.1);}
                h2 { color: #1e90ff; }
                .details { margin-top: 15px; }
                .details p { line-height: 1.6; }
                .footer { margin-top: 25px; font-size: 12px; color: #888; text-align: center; }
            </style>
        </head>
        <body>
            <div class="container">
                <h2>Appointment Confirmation</h2>
                <p>Dear <strong>%s</strong>,</p>
                <p>Your appointment has been successfully booked at <strong>CareOptics</strong>.</p>
                <div class="details">
                    <p><strong>Date:</strong> %s</p>
                    <p><strong>Time:</strong> %s</p>
                </div>
                <p>Please visit our store to consult with our eye doctor. We look forward to seeing you!</p>
                <div class="footer">
                    &copy; 2025 CareOptics. All rights reserved.
                </div>
            </div>
        </body>
        </html>
        """.formatted(userName, appointmentDate, appointmentTime);

        emailService.sendHtmlEmail(userEmail, subject, body);
    }

    private void sendAppointmentNotificationToAdmin(String adminEmail, String userName,
                                                    String userEmail, String phone,
                                                    String appointmentDate, String appointmentTime,
                                                    String problems) {
        String subject = "New Appointment Booking - SpecsShope";

        String body = """
        <html>
        <head>
            <style>
                body { font-family: Arial, sans-serif; background-color: #f4f4f9; color: #333; }
                .container { max-width: 600px; margin: 20px auto; background-color: #fff; padding: 20px; border-radius: 10px; box-shadow: 0 4px 6px rgba(0,0,0,0.1);}
                h2 { color: #ff4500; }
                .details { margin-top: 15px; }
                .details p { line-height: 1.6; }
                .footer { margin-top: 25px; font-size: 12px; color: #888; text-align: center; }
            </style>
        </head>
        <body>
            <div class="container">
                <h2>New Appointment Booking</h2>
                <div class="details">
                    <p><strong>Name:</strong> %s</p>
                    <p><strong>Email:</strong> %s</p>
                    <p><strong>Phone:</strong> %s</p>
                    <p><strong>Date:</strong> %s</p>
                    <p><strong>Time:</strong> %s</p>
                    <p><strong>Eye Problems:</strong> %s</p>
                </div>
                <p>Thank you for helping someone see better today!</p>
                <div class="footer">
                    &copy; 2025 SpecsShope. All rights reserved.
                </div>
            </div>
        </body>
        </html>
        """.formatted(userName, userEmail, phone, appointmentDate, appointmentTime, problems);

        emailService.sendHtmlEmail(adminEmail, subject, body);
    }
    @Override
    public List<String> getAvailableSlots(String preferredDate) {
        List<String> allSlots = List.of(
                "09:00 AM", "09:30 AM", "10:00 AM", "10:30 AM",
                "11:00 AM", "11:30 AM", "12:00 PM", "12:30 PM",
                "01:00 PM", "01:30 PM", "02:00 PM", "02:30 PM",
                "03:00 PM", "03:30 PM", "04:00 PM", "04:30 PM",
                "05:00 PM"
        ).stream().map(String::toUpperCase).collect(Collectors.toList());

        List<Appointment> booked = appointmentRepository.findByPreferredDate(preferredDate);
        Set<String> bookedSlots = booked.stream()
                .map(a -> a.getPreferredTime().trim().toUpperCase())
                .collect(Collectors.toSet());

        return allSlots.stream()
                .filter(slot -> !bookedSlots.contains(slot))
                .collect(Collectors.toList());
    }
    @Override
    public List<Map<String, Object>> getAppointmentsSummary() {
        List<Appointment> allAppointments = appointmentRepository.findAll();

        Map<String, Long> counts = allAppointments.stream()
                .collect(Collectors.groupingBy(Appointment::getPreferredDate, Collectors.counting()));

        List<Map<String, Object>> summary = counts.entrySet().stream()
                .map(e -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("date", e.getKey());
                    map.put("count", e.getValue());
                    return map;
                })
                .collect(Collectors.toList());

        return summary;
    }
    @Override
    public List<AppointmentResponseDTO> getAppointmentsByDate(String date) {
        List<Appointment> appointments = appointmentRepository.findByPreferredDate(date);
        return appointments.stream()
                .map(appointmentMapper::toDto)
                .collect(Collectors.toList());
    }
    @Override
    public List<AppointmentResponseDTO> getRecentAppointments(int limit) {
        List<Appointment> appointments = appointmentRepository
                .findTopByOrderByBookedAtDesc(limit); // custom repo query
        return appointments.stream()
                .map(appointmentMapper::toDto)
                .toList();
    }
    @Override
    public List<AppointmentReasonStatsDTO> countAppointmentsByReason() {
        return appointmentRepository.countAppointmentsByReason().stream()
                .map(r -> new AppointmentReasonStatsDTO(r.getReason(), r.getCount()))
                .toList();
    }


}