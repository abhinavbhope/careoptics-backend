package com.specsShope.specsBackend.Controller;

import com.specsShope.specsBackend.Dtos.*;
import com.specsShope.specsBackend.Models.CustomUserDetails;
import com.specsShope.specsBackend.Models.User;
import com.specsShope.specsBackend.Services.DoctorAppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctor-appointments")
@RequiredArgsConstructor
public class DoctorAppointmentController {

    private final DoctorAppointmentService service;

    /* ========== PUBLIC – walk-in (no token) ========== */
    @PostMapping("/walk-in")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DoctorAppointmentResponse> walkIn(
            @Valid @RequestBody WalkInAppointmentRequest dto) {

        DoctorAppointmentRequest req = DoctorAppointmentRequest.builder()
                .patientName(dto.getPatientName())
                .age(dto.getAge())
                .phone(dto.getPhone())
                .address(dto.getAddress())
                .reasonForVisit(dto.getReasonForVisit())
                .appointmentDate(dto.getAppointmentDate())
                .userId(null) // walk-in
                .build();

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.create(req, null, false));
    }

    /* ========== REGISTERED USER ========== */
    @PostMapping("/my-booking")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<DoctorAppointmentResponse> createForSelf(
            @Valid @RequestBody DoctorAppointmentRequest dto,
            @AuthenticationPrincipal CustomUserDetails principal) {

        // Set the logged-in user's ID automatically
        dto.setUserId(principal.getUserId());

        DoctorAppointmentResponse appt = service.create(dto, principal.getUserId(), false);
        return ResponseEntity.status(HttpStatus.CREATED).body(appt);
    }


    @GetMapping("/my")
    @PreAuthorize("hasRole('USER')")
    public List<DoctorAppointmentResponse> myHistory(@AuthenticationPrincipal CustomUserDetails principal) {
        return service.getAllForUser(principal.getUserId());
    }

    @GetMapping("/my/{id}")
    @PreAuthorize("hasRole('USER')")
    public DoctorAppointmentResponse getMine(@PathVariable String id,
                                             @AuthenticationPrincipal CustomUserDetails principal) {
        return service.getById(id, principal.getUserId(), false);
    }

    /* ========== ADMIN ========== */
    @PostMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DoctorAppointmentResponse> createForAnyUser(
            @Valid @RequestBody DoctorAppointmentRequest dto) {
        // Admin can specify any userId
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.create(dto, null, true));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public DoctorAppointmentResponse update(@PathVariable String id,
                                            @Valid @RequestBody DoctorAppointmentRequest dto) {
        return service.update(id, dto, null, true);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        service.delete(id, null, true);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<DoctorAppointmentResponse> userHistory(@PathVariable String userId) {
        return service.getAllForUser(userId);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public DoctorAppointmentResponse getOne(@PathVariable String id) {
        return service.getById(id, null, true);
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public List<DoctorAppointmentResponse> searchByPhone(@RequestParam String phone) {
        return service.searchByPhone(phone);
    }
    @GetMapping("/admin/users/search")
    public ResponseEntity<List<UserResponse>> searchRegisteredUsers(@RequestParam String keyword) {
        return ResponseEntity.ok(service.searchRegisteredUsers(keyword));
    }


}