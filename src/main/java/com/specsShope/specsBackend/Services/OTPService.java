package com.specsShope.specsBackend.Services;

import jakarta.mail.MessagingException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class OTPService {

    private final EmailService emailService; // Inject your existing EmailService

    private final Map<String, OTP> otpStorage = new ConcurrentHashMap<>();
    private final Set<String> verifiedEmails = ConcurrentHashMap.newKeySet();
    public boolean isEmailVerified(String email) {
        return verifiedEmails.contains(email);
    }
    public static class OTP {
        private final String email;
        @Getter
        private final String otp;
        private final LocalDateTime expiryTime;

        public OTP(String email, String otp, LocalDateTime expiryTime) {
            this.email = email;
            this.otp = otp;
            this.expiryTime = expiryTime;
        }

        public boolean isExpired() {
            return LocalDateTime.now().isAfter(expiryTime);
        }

    }

    public String generateOTP() {
        int otp = 100000 + new Random().nextInt(900000);
        return String.valueOf(otp);
    }

    public void sendOtp(String email) {
        String otp = generateOTP();
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(10);
        otpStorage.put(email, new OTP(email, otp, expiry));

        String html = "<h2>Your OTP code is: <strong>" + otp + "</strong></h2>" +
                "<p>This code will expire in 10 minutes.</p>";
        emailService.sendHtmlEmail(email, "Your CareOptics OTP Code", html);
    }

    public boolean verifyOtp(String email, String otp) {
        OTP stored = otpStorage.get(email);
        if (stored == null || stored.isExpired()) {
            otpStorage.remove(email);
            return false;
        }
        boolean match = stored.getOtp().equals(otp);
        if (match) {
            verifiedEmails.add(email);   // keep the flag
            otpStorage.remove(email);    // remove OTP
        }
        return match;
    }
    public void clearVerified(String email) {
        verifiedEmails.remove(email);
    }

}
