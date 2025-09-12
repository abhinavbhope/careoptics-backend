package com.specsShope.specsBackend.Services;

import com.specsShope.specsBackend.Dtos.*;
import com.specsShope.specsBackend.Mappers.EyeTestMapper;
import com.specsShope.specsBackend.Models.EyeTestRecord;
import com.specsShope.specsBackend.Models.Role;
import com.specsShope.specsBackend.Models.User;
import com.specsShope.specsBackend.Repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EyeTestServiceImpl implements EyeTestService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final OTPService otpService;
    private final EmailService emailService;
    private static final String CHAR_POOL =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    /* ========== CREATE ========== */

    @Override
    public EyeTestResponseDTO createForExternalUser(ExternalEyeTestFormDTO dto) {

        // 1. If the user already exists, skip OTP and just add the test.
        User user = userRepo.findByEmail(dto.getEmail())
                .orElseGet(() -> createAndSaveNewUser(dto));

        // 2. Build and attach the eye-test record.
        EyeTestRecord firstTest = EyeTestMapper.toEntity(dto, user.getId());
        firstTest.setTestId(UUID.randomUUID().toString());
        firstTest.setTestDate(LocalDate.now());

        user.getEyeTestHistory().add(firstTest);
        userRepo.save(user);

        // 3. Welcome mail only on first registration.
        if (userRepo.findByEmail(dto.getEmail()).isEmpty()) {
            emailService.sendEmail(
                    dto.getEmail(),
                    "Welcome to CareOptics – Account Created",
                    "Your account has been created. Use ‘Forgot Password’ to set your own password."
            );
        }

        return EyeTestMapper.toResponseDTO(firstTest, user);
    }

    /* ------------- helper ------------- */
    private User createAndSaveNewUser(ExternalEyeTestFormDTO dto) {
        if (!otpService.isEmailVerified(dto.getEmail())) {
            throw new RuntimeException("Email not verified. Please verify OTP first.");
        }

        String tempPassword = generateRandomPassword(8);

        User newUser = User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .address(dto.getAddress())
                .username(dto.getEmail())
                .age(dto.getAge())
                .password(passwordEncoder.encode(tempPassword))
                .role(Role.USER)
                .external(true)
                .createdAt(LocalDateTime.now())
                .eyeTestHistory(new ArrayList<>())
                .build();

        // Fire welcome e-mail only once
        emailService.sendEmail(
                dto.getEmail(),
                "Welcome to CareOptics – Account Created",
                "Your account has been created.\n\n" +
                        "Login using:\n" +
                        "Email: " + dto.getEmail() + "\n" +
                        "Temporary password: " + tempPassword + "\n\n" +
                        "Please change it after logging in using 'Forgot Password'."
        );

        return userRepo.save(newUser);
    }

    @Override
    public EyeTestResponseDTO createForExistingUser(String userId, EyeTestFormRequestDTO dto) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        // 🔹 Update personal fields if provided in the DTO
        if (dto.getAge() != null) user.setAge(dto.getAge());
        if (dto.getName() != null && !dto.getName().isBlank()) user.setName(dto.getName());
        if (dto.getPhone() != null && !dto.getPhone().isBlank()) user.setPhone(dto.getPhone());
        if (dto.getAddress() != null && !dto.getAddress().isBlank()) user.setAddress(dto.getAddress());

        // 🔹 Build and attach new eye test
        EyeTestRecord newTest = EyeTestMapper.toEntity(dto, user.getId());
        newTest.setTestId(UUID.randomUUID().toString());
        newTest.setTestDate(LocalDate.now());

        if (user.getEyeTestHistory() == null) {
            user.setEyeTestHistory(new ArrayList<>());
        }
        user.getEyeTestHistory().add(newTest);

        User savedUser = userRepo.save(user);
        return EyeTestMapper.toResponseDTO(newTest, savedUser);
    }

    /* ========== READ ========== */

    @Override
    public List<EyeTestResponseDTO> getHistoryForUser(String userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        if (user.getEyeTestHistory() == null || user.getEyeTestHistory().isEmpty()) {
            return new ArrayList<>();
        }

        // Return clean history - each test is independent
        return user.getEyeTestHistory().stream()
                .map(test -> EyeTestMapper.toResponseDTO(test, user))
                .collect(Collectors.toList());
    }

    @Override
    public List<EyeTestResponseDTO> getHistoryForUserAndDateRange(String userId,
                                                                  LocalDate startDate,
                                                                  LocalDate endDate) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        if (user.getEyeTestHistory() == null || user.getEyeTestHistory().isEmpty()) {
            return new ArrayList<>();
        }

        // Filter by date range
        return user.getEyeTestHistory().stream()
                .filter(test -> {
                    LocalDate testDate = test.getTestDate();
                    return testDate != null &&
                            (testDate.isEqual(startDate) || testDate.isAfter(startDate)) &&
                            (testDate.isEqual(endDate) || testDate.isBefore(endDate));
                })
                .map(test -> EyeTestMapper.toResponseDTO(test, user))
                .collect(Collectors.toList());
    }

    @Override
    public EyeTestResponseDTO getLatestForUser(String userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        if (user.getEyeTestHistory() == null || user.getEyeTestHistory().isEmpty()) {
            throw new RuntimeException("No eye test records found for user: " + userId);
        }

        // Get latest test (last in the list)
        EyeTestRecord latestTest = user.getEyeTestHistory()
                .get(user.getEyeTestHistory().size() - 1);

        return EyeTestMapper.toResponseDTO(latestTest, user);
    }

    /* ========== UPDATE ========== */

    @Override
    public EyeTestResponseDTO updateEyeTest(String testId, String userId, EyeTestFormRequestDTO dto) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        if (user.getEyeTestHistory() == null) {
            throw new RuntimeException("No eye test history found for user");
        }

        // 🔹 Update personal fields from DTO (if provided)
        if (dto.getAge() != null) user.setAge(dto.getAge());
        if (dto.getName() != null && !dto.getName().isBlank()) user.setName(dto.getName());
        if (dto.getPhone() != null && !dto.getPhone().isBlank()) user.setPhone(dto.getPhone());
        if (dto.getAddress() != null && !dto.getAddress().isBlank()) user.setAddress(dto.getAddress());

        // 🔹 Find the specific test to update
        EyeTestRecord testToUpdate = user.getEyeTestHistory().stream()
                .filter(test -> test.getTestId().equals(testId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Eye test not found with ID: " + testId));

        // 🔹 Update test fields only
        updateEyeTestFields(testToUpdate, dto);

        // 🔹 Save user (both user info + updated test record)
        User savedUser = userRepo.save(user);

        return EyeTestMapper.toResponseDTO(testToUpdate, savedUser);
    }


    /* ========== DELETE ========== */

    @Override
    public void deleteEyeTest(String testId, String userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        if (user.getEyeTestHistory() == null) {
            throw new RuntimeException("No eye test history found for user");
        }

        boolean removed = user.getEyeTestHistory()
                .removeIf(test -> test.getTestId().equals(testId));

        if (!removed) {
            throw new RuntimeException("Eye test not found with ID: " + testId);
        }

        userRepo.save(user);
    }

    /* ========== HELPER METHODS ========== */

    private void updateEyeTestFields(EyeTestRecord record, EyeTestFormRequestDTO dto) {
        record.setDvRightEye(EyeTestMapper.toEntity(dto.getDvRightEye()));
        record.setDvLeftEye(EyeTestMapper.toEntity(dto.getDvLeftEye()));
        record.setNvRightEye(EyeTestMapper.toEntity(dto.getNvRightEye()));
        record.setNvLeftEye(EyeTestMapper.toEntity(dto.getNvLeftEye()));
        record.setImRightEye(EyeTestMapper.toEntity(dto.getImRightEye()));
        record.setImLeftEye(EyeTestMapper.toEntity(dto.getImLeftEye()));
        record.setFrame(dto.getFrame());
        record.setLens(dto.getLens());
        record.setNotes(dto.getNotes());
        record.setBookingDate(dto.getBookingDate());
        record.setDeliveryDate(dto.getDeliveryDate());
    }

    /* ========== ADDITIONAL UTILITY METHODS FOR JWT/ADMIN ========== */

    /**
     * Create eye test for existing user by email (Admin operation)
     * Admin searches user by email and adds eye test
     */
    public EyeTestResponseDTO createForExistingUserByEmail(String email, EyeTestFormRequestDTO dto) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        EyeTestResponseDTO response = createForExistingUser(user.getId(), dto);

        // 📧 Styled HTML email
        String htmlBody = "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "  <style>" +
                "    body { font-family: Arial, sans-serif; background-color: #f9f9f9; color: #333; padding: 20px; }" +
                "    .container { max-width: 600px; margin: auto; background: #ffffff; padding: 20px; border-radius: 12px; box-shadow: 0 4px 12px rgba(0,0,0,0.1); }" +
                "    h2 { color: #2E86C1; }" +
                "    p { font-size: 15px; line-height: 1.6; }" +
                "    .button { display: inline-block; margin-top: 20px; padding: 12px 20px; font-size: 16px; color: white; background-color: #2E86C1; border-radius: 8px; text-decoration: none; }" +
                "    .footer { margin-top: 30px; font-size: 12px; color: #888; text-align: center; }" +
                "  </style>" +
                "</head>" +
                "<body>" +
                "  <div class='container'>" +
                "    <h2>👓 New Eye Test Record Added</h2>" +
                "    <p>Hello <b>" + user.getName() + "</b>,</p>" +
                "    <p>A new eye test record has been added to your profile by our CareOptics team.</p>" +
                "    <p><b>Test Date:</b> " + LocalDate.now() + "</p>" +
                "    <p>Please log in to your account to check the results under <b>My Eye Tests</b>.</p>" +
                "    <a class='button' href='https://careoptics.vercel.app/profile'>View My Profile</a>" +
                "    <div class='footer'>CareOptics &copy; " + LocalDate.now().getYear() + " | All rights reserved</div>" +
                "  </div>" +
                "</body>" +
                "</html>";

        emailService.sendHtmlEmail(
                user.getEmail(),
                "New Eye Test Added to Your Profile",
                htmlBody
        );

        return response;
    }


    /**
     * Get user history by email (Admin operation)
     */
    public List<EyeTestResponseDTO> getHistoryForUserByEmail(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        return getHistoryForUser(user.getId());
    }

    /**
     * Extract userId from JWT token and create eye test
     */
    public EyeTestResponseDTO createForAuthenticatedUser(String jwtToken, EyeTestFormRequestDTO dto) {
        String userId = jwtService.extractUserId(jwtToken.replace("Bearer ", ""));
        return createForExistingUser(userId, dto);
    }

    /**
     * Extract userId from JWT token and get history
     */
    public List<EyeTestResponseDTO> getHistoryForAuthenticatedUser(String jwtToken) {
        String userId = jwtService.extractUserId(jwtToken.replace("Bearer ", ""));
        return getHistoryForUser(userId);
    }

    /**
     * Send OTP for external user verification
     */
    public void sendVerificationOTP(String email) {
        // Check if user already exists
        if (userRepo.findByEmail(email).isPresent()) {
            throw new RuntimeException("User already exists with email: " + email);
        }

        otpService.sendOtp(email);
    }

    /**
     * Verify OTP before allowing external user creation
     */
    public boolean verifyOTPForExternalUser(String email, String otp) {
        return otpService.verifyOtp(email, otp);
    }

    private static final SecureRandom RANDOM = new SecureRandom();

    private String generateRandomPassword(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(CHAR_POOL.charAt(RANDOM.nextInt(CHAR_POOL.length())));
        }
        return sb.toString();

    }
}