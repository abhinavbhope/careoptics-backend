package com.specsShope.specsBackend.Controller;


import com.specsShope.specsBackend.Dtos.*;
import com.specsShope.specsBackend.Models.CustomUserDetails;
import com.specsShope.specsBackend.Models.Role;
import com.specsShope.specsBackend.Models.User;
import com.specsShope.specsBackend.Repository.UserRepo;
import com.specsShope.specsBackend.Services.AuthService;
import com.specsShope.specsBackend.Services.JwtService;
import com.specsShope.specsBackend.Services.OTPService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;
@Slf4j
@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final OTPService otpService;
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        if (userRepo.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already registered");
        }

        // ✅ Check if email was verified via OTP
        if (!otpService.isEmailVerified(request.getEmail())) {
            return ResponseEntity.badRequest().body("Email not verified via OTP");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .name(request.getName())
                .age(request.getAge())
                .address(request.getAddress())
                .role(Role.USER)
                .build();

        userRepo.save(user);

        // ✅ (Optional) Clear email from verified set after successful registration
        otpService.clearVerified(request.getEmail());

        return ResponseEntity.ok("User registered successfully as USER");
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/register-admin")
    public ResponseEntity<String> registerAdmin(@RequestBody RegisterRequest request) {
        if (userRepo.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already registered");
        }

        if (request.getRole() == null || !request.getRole().equals(Role.ADMIN)) {
            return ResponseEntity.badRequest().body("Only ADMIN role is allowed here");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .name(request.getName())
                .age(request.getAge())
                .address(request.getAddress())
                .role(Role.ADMIN)
                .build();

        userRepo.save(user);
        return ResponseEntity.ok("Admin registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.login(request);   // delegate
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            log.error("Login failed for {}: {}", request.getEmail(), ex.getMessage());
            return ResponseEntity.badRequest().body(null);   // 400 with empty body
        }
    }


    @PostMapping("/send-otp")
    public ResponseEntity<String> sendOtp(@RequestBody SendOtpRequest dto) {
        otpService.sendOtp(dto.getEmail());
        return ResponseEntity.ok("OTP sent to " + dto.getEmail());
    }
    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestBody VerifyOtpRequest request) {
        boolean ok = otpService.verifyOtp(request.getEmail(), request.getOtp());
        return ok
                ? ResponseEntity.ok("OTP verified")
                : ResponseEntity.badRequest().body("Invalid or expired OTP");
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody SendOtpRequest request) {
        var userOpt = userRepo.findByEmail(request.getEmail());
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }
        otpService.sendOtp(request.getEmail());
        return ResponseEntity.ok("OTP sent to " + request.getEmail());
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        if (!otpService.isEmailVerified(request.getEmail())) {
            return ResponseEntity.badRequest().body("Email not verified via OTP");
        }

        User user = userRepo.findByEmail(request.getEmail())
                .orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found");
        }

        // ✅ Check if new password is same as old password
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body("New password cannot be the same as the old password");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepo.save(user);

        otpService.clearVerified(request.getEmail());

        return ResponseEntity.ok("Password reset successfully");
    }
    @PostMapping("/update-password")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<String> updatePassword(
            @RequestBody UpdatePasswordRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        String email = request.getEmail();
        User user = userDetails.getUser();

        // 🔒 Check email match
        if (!user.getEmail().equals(email)) {
            return ResponseEntity.badRequest().body("Email mismatch");
        }

        // 🔐 Check OTP verified
        if (!otpService.isEmailVerified(email)) {
            return ResponseEntity.badRequest().body("Email not verified via OTP");
        }

        // 🔒 Check current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body("Current password is incorrect");
        }

        // 🔄 Same password check
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body("New password cannot be the same as the current password");
        }

        // ✅ Update
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepo.save(user);
        otpService.clearVerified(email);

        return ResponseEntity.ok("Password updated successfully");
    }

}
