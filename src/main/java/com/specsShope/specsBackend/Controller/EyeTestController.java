package com.specsShope.specsBackend.Controller;

import com.specsShope.specsBackend.Dtos.*;
import com.specsShope.specsBackend.Services.EyeTestService;   // <-- interface only
import com.specsShope.specsBackend.Services.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/eye-tests")
@RequiredArgsConstructor
public class EyeTestController {

    private final EyeTestService eyeTestService;   // <-- interface only
    private final JwtService jwtService;

    /* =====================================================
     *  1. OTP FLOW FOR EXTERNAL/WALK-IN USERS (NO LOGIN)
     * ===================================================== */

    // Anyone (guest / kiosk / SPA) can request OTP for external users
    @PostMapping("/external/send-otp")
    public ResponseEntity<Map<String, String>> sendOtp(@Valid @RequestBody SendOtpRequest request) {
        eyeTestService.sendVerificationOTP(request.getEmail());
        return ResponseEntity.ok(Map.of("message", "OTP sent", "email", request.getEmail()));
    }

    // Anyone can verify OTP (external users only, no JWT required)
    @PostMapping("/external/verify-otp")
    public ResponseEntity<Map<String, Object>> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        boolean ok = eyeTestService.verifyOTPForExternalUser(request.getEmail(), request.getOtp());
        return ok
                ? ResponseEntity.ok(Map.of("message", "OTP verified", "verified", true))
                : ResponseEntity.badRequest().body(Map.of("message", "Invalid or expired OTP", "verified", false));
    }

    /* =====================================================
     *  2. EXTERNAL USER EYE-TEST (AUTO ACCOUNT CREATION)
     * ===================================================== */

    // Receptionist / kiosk / guest (after OTP verified) can register + submit eye-test
    @PostMapping("/external/register-and-test")
    public ResponseEntity<EyeTestResponseDTO> registerExternalAndTest(
            @Valid @RequestBody ExternalEyeTestFormDTO dto) {

        EyeTestResponseDTO response = eyeTestService.createForExternalUser(dto);
        return ResponseEntity.ok(response);
    }

    /* =====================================================
     *  3. AUTHENTICATED USER (ROLE_USER)
     * ===================================================== */

    // Logged-in USER can view their full test history
    @GetMapping("/my-history")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<EyeTestResponseDTO>> getMyHistory(
            @RequestHeader("Authorization") String authHeader) {

        String userId = jwtService.extractUserId(authHeader.replace("Bearer ", ""));
        return ResponseEntity.ok(eyeTestService.getHistoryForUser(userId));
    }

    // Logged-in USER can view their latest eye-test
    @GetMapping("/my-latest")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<EyeTestResponseDTO> getMyLatest(
            @RequestHeader("Authorization") String authHeader) {

        String userId = jwtService.extractUserId(authHeader.replace("Bearer ", ""));
        return ResponseEntity.ok(eyeTestService.getLatestForUser(userId));
    }

    // Logged-in USER can view tests between date range
    @GetMapping("/my-history/date-range")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<EyeTestResponseDTO>> getHistoryByDateRange(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {

        String userId = jwtService.extractUserId(authHeader.replace("Bearer ", ""));
        return ResponseEntity.ok(
                eyeTestService.getHistoryForUserAndDateRange(userId, startDate, endDate));
    }

    /* =====================================================
     *  4. ADMIN APIs (ROLE_ADMIN)
     * ===================================================== */

    // ADMIN can create a test for an existing user (by email)
    @PostMapping("/admin/user/{email}/tests")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EyeTestResponseDTO> createForUserByEmail(
            @PathVariable String email,
            @Valid @RequestBody EyeTestFormRequestDTO dto) {

        return ResponseEntity.ok(eyeTestService.createForExistingUserByEmail(email, dto));
    }

    // ADMIN can fetch all tests of a user (by email)
    @GetMapping("/admin/user/{email}/tests")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<EyeTestResponseDTO>> getHistoryForUserByEmail(
            @PathVariable String email) {

        return ResponseEntity.ok(eyeTestService.getHistoryForUserByEmail(email));
    }

    // ADMIN can update a specific test (by testId + email)
    @PutMapping("/admin/user/{email}/tests/{testId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EyeTestResponseDTO> updateByAdmin(
            @PathVariable String email,
            @PathVariable String testId,
            @Valid @RequestBody EyeTestFormRequestDTO dto) {

        List<EyeTestResponseDTO> history = eyeTestService.getHistoryForUserByEmail(email);
        if (history.isEmpty()) return ResponseEntity.badRequest().build();

        String userId = history.get(0).getUserId();
        return ResponseEntity.ok(eyeTestService.updateEyeTest(testId, userId, dto));
    }

    // ADMIN can delete a specific test (by testId + email)
    @DeleteMapping("/admin/user/{email}/tests/{testId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteByAdmin(
            @PathVariable String email,
            @PathVariable String testId) {

        List<EyeTestResponseDTO> history = eyeTestService.getHistoryForUserByEmail(email);
        if (history.isEmpty()) return ResponseEntity.badRequest().body(Map.of("error", "User not found"));

        String userId = history.get(0).getUserId();
        eyeTestService.deleteEyeTest(testId, userId);
        return ResponseEntity.ok(Map.of("message", "Deleted"));
    }
}