package com.specsShope.specsBackend.Services;

import com.specsShope.specsBackend.Dtos.*;
import java.time.LocalDate;
import java.util.List;

public interface EyeTestService {

    /* ---------- existing CRUD ---------- */
    EyeTestResponseDTO createForExistingUser(String userId, EyeTestFormRequestDTO dto);
    EyeTestResponseDTO createForExternalUser(ExternalEyeTestFormDTO dto);
    List<EyeTestResponseDTO> getHistoryForUser(String userId);
    List<EyeTestResponseDTO> getHistoryForUserAndDateRange(String userId,
                                                           LocalDate startDate,
                                                           LocalDate endDate);
    EyeTestResponseDTO getLatestForUser(String userId);
    EyeTestResponseDTO updateEyeTest(String testId, String userId, EyeTestFormRequestDTO dto);
    void deleteEyeTest(String testId, String userId);

    /* ---------- NEW utility methods ---------- */
    void sendVerificationOTP(String email);
    boolean verifyOTPForExternalUser(String email, String otp);
    EyeTestResponseDTO createForExistingUserByEmail(String email, EyeTestFormRequestDTO dto);
    List<EyeTestResponseDTO> getHistoryForUserByEmail(String email);
}