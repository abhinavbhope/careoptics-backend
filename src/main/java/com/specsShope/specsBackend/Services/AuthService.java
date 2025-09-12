package com.specsShope.specsBackend.Services;

import com.specsShope.specsBackend.Dtos.*;
import com.specsShope.specsBackend.Models.CustomUserDetails;
import com.specsShope.specsBackend.Models.Role;
import com.specsShope.specsBackend.Models.User;
import com.specsShope.specsBackend.Repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final OTPService otpService;
    @Autowired
    private final EmailService emailService;

    // ====================== REGISTER USER ======================
    public AuthResponse register(RegisterRequest request) {
        if (userRepo.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        if (!otpService.isEmailVerified(request.getEmail())) {
            throw new RuntimeException("Email not verified via OTP");
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
        otpService.clearVerified(request.getEmail());

        String token = jwtService.generateToken(new CustomUserDetails(user));
        return new AuthResponse(
                token,
                user.getRole().name(),
                user.getId(),
                user.getName(),
                user.getAddress(),   // 5th
                user.getAge()        // 6th
        );

    }

    // ====================== REGISTER ADMIN ======================
    public AuthResponse registerAdmin(RegisterRequest request) {
        if (userRepo.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        if (request.getRole() == null || !request.getRole().equals(Role.ADMIN)) {
            throw new RuntimeException("Only ADMIN role is allowed here");
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

        String token = jwtService.generateToken(new CustomUserDetails(user));
        return new AuthResponse(
                token,
                user.getRole().name(),
                user.getId(),
                user.getName(),
                user.getAddress(),   // 5th
                user.getAge()        // 6th
        );

    }

    // ====================== LOGIN ======================
    // ====================== LOGIN ======================
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser(); // Extract User entity from CustomUserDetails
        String token = jwtService.generateToken(userDetails);

        // ✅ Send Welcome Email
        String subject = "Welcome back to CareOptics, " + user.getName() + "!";
        String htmlBody = """
        <html>
          <body style="font-family: Arial, sans-serif; background-color:#f4f4f4; margin:0; padding:20px;">
            <table align="center" cellpadding="0" cellspacing="0" width="600" 
                   style="border:2px solid #2E86C1; border-radius:12px; background-color:#ffffff; box-shadow:0 4px 12px rgba(0,0,0,0.1);">
              
              <!-- HEADER -->
              <tr>
                <td style="text-align:center; padding:20px; border-bottom:2px solid #2E86C1; background:#f9fbfd;">
                  <h1 style="color:#2E86C1; margin:0;">👓 CareOptics</h1>
                </td>
              </tr>
              
              <!-- BODY -->
              <tr>
                <td style="padding:30px; text-align:left;">
                  <h2 style="color:#333; margin-top:0;">Hi %s 👋</h2>
                  <p style="color:#555; font-size:16px; line-height:1.6; margin:15px 0;">
                    Welcome back to <b>OptiCare</b>! We're so happy to see you again.
                  </p>
                  <p style="color:#555; font-size:16px; line-height:1.6; margin:15px 0;">
                    Discover our newest eyewear collections, book appointments, and take care of your vision effortlessly.
                  </p>
                  
                  <!-- CTA BUTTON -->
                  <div style="margin-top:25px; text-align:center;">
                  
                  </div>
                </td>
              </tr>
              
              <!-- FOOTER -->
              <tr>
                <td style="padding:20px; text-align:center; border-top:1px solid #eee; color:#999; font-size:12px; background:#f9fbfd; border-radius:0 0 12px 12px;">
                  <p style="margin:0;">This is an automated message, please do not reply.</p>
                  <p style="margin:5px 0;">&copy; 2025 OptiCare. All rights reserved.</p>
                </td>
              </tr>
            </table>
          </body>
        </html>
        """.formatted(user.getName());

        emailService.sendHtmlEmail(user.getEmail(), subject, htmlBody);

        // ✅ Return AuthResponse
        return new AuthResponse(
                token,
                user.getRole().name(),
                user.getId(),
                user.getName(),
                user.getAddress(),   // 5th
                user.getAge()        // 6th
        );
    }


    // ====================== OTP ======================
    public String sendOtp(String email) {
        otpService.sendOtp(email);
        return "OTP sent to " + email;
    }

    public String verifyOtp(VerifyOtpRequest request) {
        return otpService.verifyOtp(request.getEmail(), request.getOtp())
                ? "OTP verified"
                : "Invalid or expired OTP";
    }

    // ====================== RESET PASSWORD ======================
    public String resetPassword(ResetPasswordRequest request) {
        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("No user found with this email"));

        if (!otpService.isEmailVerified(request.getEmail())) {
            return "Email not verified via OTP";
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            return "This password was used previously. Please choose a different one.";
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepo.save(user);
        otpService.clearVerified(request.getEmail());
        return "Password reset successful";
    }
}
