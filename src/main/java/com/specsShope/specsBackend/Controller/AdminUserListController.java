package com.specsShope.specsBackend.Controller;

import com.specsShope.specsBackend.Dtos.*;
import com.specsShope.specsBackend.Services.DoctorAppointmentService;
import com.specsShope.specsBackend.Services.DoctorPastUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserListController {

    private final DoctorAppointmentService appointmentService;
    private final DoctorPastUserService pastUserService;

    /* 1. LIST  –  all registered users with appointments */
    @GetMapping("/registered")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllRegistered() {
        return ResponseEntity.ok(appointmentService.searchRegisteredUsers("")); // empty keyword = all
    }

    /* 2. SEARCH  –  registered users by name/email */
    @GetMapping("/registered/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> searchRegistered(@RequestParam String keyword) {
        return ResponseEntity.ok(appointmentService.searchRegisteredUsers(keyword));
    }

    /* 3. LIST – all past users */
    @GetMapping("/past")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<DoctorPastUserResponse>> getAllPast() {
        return ResponseEntity.ok(pastUserService.listAll());
    }

    /* 4. LIST – every walk-in appointment */
    @GetMapping("/walk-in")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<DoctorAppointmentResponse>> getAllWalkIn() {
        return ResponseEntity.ok(appointmentService.searchByPhone(null));
    }
}