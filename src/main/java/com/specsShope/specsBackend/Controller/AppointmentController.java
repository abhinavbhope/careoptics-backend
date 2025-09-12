package com.specsShope.specsBackend.Controller;

import com.specsShope.specsBackend.Dtos.AppointmentReasonStatsDTO;
import com.specsShope.specsBackend.Dtos.AppointmentRequestDTO;
import com.specsShope.specsBackend.Dtos.AppointmentResponseDTO;
import com.specsShope.specsBackend.Models.CustomUserDetails;
import com.specsShope.specsBackend.Repository.AppointmentRepository;
import com.specsShope.specsBackend.Services.AppointmentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/appointments")
@SecurityRequirement(name = "bearerAuth")

public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    private final AppointmentRepository appointmentRepository;

    public AppointmentController(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    @PostMapping("/book")
    public ResponseEntity<AppointmentResponseDTO> bookAppointment(
            @RequestBody AppointmentRequestDTO requestDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            if (requestDTO == null) {
                return ResponseEntity.badRequest().build();
            }

            // 🔥 Always take userId from logged-in user
            String userId = userDetails.getUser().getId().toString();
            requestDTO.setUserId(userId);

            // Validate required fields
            if (requestDTO.getName() == null || requestDTO.getName().trim().isEmpty() ||
                    requestDTO.getEmail() == null || requestDTO.getEmail().trim().isEmpty() ||
                    requestDTO.getPhone() == null || requestDTO.getPhone().trim().isEmpty() ||
                    requestDTO.getPreferredDate() == null || requestDTO.getPreferredDate().trim().isEmpty() ||
                    requestDTO.getPreferredTime() == null || requestDTO.getPreferredTime().trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            AppointmentResponseDTO response = appointmentService.bookAppointment(requestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(null);
        } catch (Exception e) {
            System.err.println("Error booking appointment: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping("/all")
    public ResponseEntity<List<AppointmentResponseDTO>> getAllAppointments() {
        try {
            List<AppointmentResponseDTO> appointments = appointmentService.getAllAppointments();
            return ResponseEntity.ok(appointments);
        } catch (Exception e) {
            System.err.println("Error fetching all appointments: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/user/me")
    public ResponseEntity<List<AppointmentResponseDTO>> getMyAppointments(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        String userId = userDetails.getUser().getId().toString();
        List<AppointmentResponseDTO> appointments = appointmentService.getUserAppointments(userId);
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponseDTO> getAppointmentById(@PathVariable String id) {
        try {
            if (id == null || id.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            // You'll need to add this method to your service
            // For now, returning not found
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            System.err.println("Error fetching appointment: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("/available-slots")
    public ResponseEntity<List<String>> getAvailableSlots(@RequestParam String date) {
        if (date == null || date.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        List<String> availableSlots = appointmentService.getAvailableSlots(date);
        return ResponseEntity.ok(availableSlots);
    }
    @GetMapping("/summary")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Map<String, Object>>> getAppointmentsSummary() {
        try {
            List<Map<String, Object>> summary = appointmentService.getAppointmentsSummary();
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            System.err.println("Error fetching appointments summary: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("/by-date")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AppointmentResponseDTO>> getAppointmentsByDate(@RequestParam String date) {
        try {
            if (date == null || date.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            List<AppointmentResponseDTO> appointments = appointmentService.getAppointmentsByDate(date);
            return ResponseEntity.ok(appointments);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("/recent")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AppointmentResponseDTO>> getRecentAppointments(
            @RequestParam(defaultValue = "5") int limit) {
        try {
            List<AppointmentResponseDTO> recentAppointments = appointmentService.getRecentAppointments(limit);
            return ResponseEntity.ok(recentAppointments);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("/by-reason")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AppointmentReasonStatsDTO>> getAppointmentsByReason() {
        return ResponseEntity.ok(appointmentService.countAppointmentsByReason());
    }
}