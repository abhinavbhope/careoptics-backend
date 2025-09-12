package com.specsShope.specsBackend.Controller;

import com.specsShope.specsBackend.Dtos.*;
import com.specsShope.specsBackend.Services.DoctorPastUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/doctor-past-users")
@RequiredArgsConstructor
public class DoctorPastUserController {

    private final DoctorPastUserService service;

    /* 1. create patient (first step in admin flow) */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createPatient(@Valid @RequestBody DoctorPastUserRequest dto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(service.createPatient(dto));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", ex.getMessage())); // 👈 always "message"
        }
    }

    /* 2. list all past patients */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<DoctorPastUserResponse> listAll() {
        return service.listAll();
    }

    /* 3. append a new appointment to this patient */
    @PostMapping("/{pastUserId}/appointments")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DoctorAppointmentResponse> addAppointment(
            @PathVariable String pastUserId,
            @Valid @RequestBody AddAppointmentToPastUserRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.addAppointment(pastUserId, dto));
    }

    /* 4. get all appointments of this past patient */
    @GetMapping("/{pastUserId}/appointments")
    @PreAuthorize("hasRole('ADMIN')")
    public List<DoctorAppointmentResponse> getAppointments(@PathVariable String pastUserId) {
        return service.getAppointmentsByPastUser(pastUserId);
    }
    @PutMapping("/{pastUserId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updatePatient(
            @PathVariable String pastUserId,
            @Valid @RequestBody DoctorPastUserRequest dto) {
        try {
            return ResponseEntity.ok(service.updatePatient(pastUserId, dto));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", ex.getMessage()));
        }
    }

    @DeleteMapping("/{pastUserId}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePastUser(@PathVariable String pastUserId) {
        service.deletePastUser(pastUserId);
    }
    /* 5. search past users by phone or name */
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public List<DoctorPastUserResponse> searchPastUsers(@RequestParam String keyword) {
        return service.searchPastUsers(keyword);
    }



}