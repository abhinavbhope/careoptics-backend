package com.specsShope.specsBackend.Mappers;

import com.specsShope.specsBackend.Dtos.*;
import com.specsShope.specsBackend.Models.EyeMeasurement;
import com.specsShope.specsBackend.Models.EyeTestRecord;
import com.specsShope.specsBackend.Models.User;

import java.util.List;
import java.util.stream.Collectors;

public class EyeTestMapper {

    /* ==================================================================
       1.  EXISTING-USER FLOW
       ================================================================== */
    public static EyeTestRecord toEntity(EyeTestFormRequestDTO dto, String userId) {
        if (dto == null) return null;
        return EyeTestRecord.builder()
                .userId(userId)
                .dvRightEye(toEntity(dto.getDvRightEye()))
                .dvLeftEye(toEntity(dto.getDvLeftEye()))
                .nvRightEye(toEntity(dto.getNvRightEye()))
                .nvLeftEye(toEntity(dto.getNvLeftEye()))
                .imRightEye(toEntity(dto.getImRightEye()))
                .imLeftEye(toEntity(dto.getImLeftEye()))
                .frame(dto.getFrame())
                .lens(dto.getLens())
                .notes(dto.getNotes())
                .bookingDate(dto.getBookingDate())
                .deliveryDate(dto.getDeliveryDate())
                .testDate(dto.getTestDate())
                .build();
    }

    /* ==================================================================
       2.  EXTERNAL-USER FLOW
       ================================================================== */
    public static EyeTestRecord toEntity(ExternalEyeTestFormDTO dto, String userId) {
        if (dto == null) return null;
        return EyeTestRecord.builder()
                .userId(userId)
                .dvRightEye(toEntity(dto.getDvRightEye()))
                .dvLeftEye(toEntity(dto.getDvLeftEye()))
                .nvRightEye(toEntity(dto.getNvRightEye()))
                .nvLeftEye(toEntity(dto.getNvLeftEye()))
                .imRightEye(toEntity(dto.getImRightEye()))
                .imLeftEye(toEntity(dto.getImLeftEye()))
                .frame(dto.getFrame())
                .lens(dto.getLens())
                .notes(dto.getNotes())
                .bookingDate(dto.getBookingDate())
                .deliveryDate(dto.getDeliveryDate())
                .build();
    }

    /* ==================================================================
       3.  SINGLE ENTITY -> RESPONSE (CLEAN - NO RECURSION)
       ================================================================== */
    public static EyeTestResponseDTO toResponseDTO(EyeTestRecord record, User user) {
        if (record == null) return null;

        return EyeTestResponseDTO.builder()
                .testId(record.getTestId())
                .userId(record.getUserId())
                .testDate(record.getTestDate())
                .name(user.getName())
                .age(user.getAge())
                .phone(user.getPhone())
                .email(user.getEmail())
                .address(user.getAddress())
                .dvRightEye(toDTO(record.getDvRightEye()))
                .dvLeftEye(toDTO(record.getDvLeftEye()))
                .nvRightEye(toDTO(record.getNvRightEye()))
                .nvLeftEye(toDTO(record.getNvLeftEye()))
                .imRightEye(toDTO(record.getImRightEye()))
                .imLeftEye(toDTO(record.getImLeftEye()))
                .frame(record.getFrame())
                .lens(record.getLens())
                .notes(record.getNotes())
                .bookingDate(record.getBookingDate())
                .deliveryDate(record.getDeliveryDate())
                // ✅ REMOVED RECURSIVE MAPPING - NO MORE previousTests!
                .build();
    }

    /* ==================================================================
       4.  USER WITH FULL HISTORY (NEW METHOD)
       ================================================================== */
    public static UserEyeTestHistoryDTO toUserHistoryDTO(User user) {
        if (user == null) return null;

        List<EyeTestResponseDTO> history = user.getEyeTestHistory() != null
                ? user.getEyeTestHistory().stream()
                .map(record -> toResponseDTO(record, user))
                .collect(Collectors.toList())
                : List.of();

        return UserEyeTestHistoryDTO.builder()
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .address(user.getAddress())
                .age(user.getAge())
                .eyeTestHistory(history)
                .build();
    }

    /* ==================================================================
       5.  MEASUREMENT HELPERS
       ================================================================== */
/* ==================================================================
   5.  MEASUREMENT HELPERS (NULL-SAFE)
   ================================================================== */
    public static EyeMeasurement toEntity(EyeMeasurementDTO dto) {
        if (dto == null) return null;
        return EyeMeasurement.builder()
                .sph(dto.getSph() != null ? dto.getSph() : null)
                .cyl(dto.getCyl() != null ? dto.getCyl() : null)
                .axis(dto.getAxis() != null ? dto.getAxis() : null)
                .add(dto.getAdd() != null ? dto.getAdd() : null)
                .vision(dto.getVision() != null ? dto.getVision() : null)
                .build();
    }

    private static EyeMeasurementDTO toDTO(EyeMeasurement entity) {
        if (entity == null) return null;
        return EyeMeasurementDTO.builder()
                .sph(entity.getSph() != null ? entity.getSph() : null)
                .cyl(entity.getCyl() != null ? entity.getCyl() : null)
                .axis(entity.getAxis() != null ? entity.getAxis() : null)
                .add(entity.getAdd() != null ? entity.getAdd() : null)
                .vision(entity.getVision() != null ? entity.getVision() : null)
                .build();
    }

    /* ==================================================================
       6.  UTILITY METHOD (KEPT FOR BACKWARD COMPATIBILITY)
       ================================================================== */
    public static EyeTestRecord shallowCopy(EyeTestRecord src) {
        if (src == null) return null;
        return EyeTestRecord.builder()
                .testId(src.getTestId())
                .userId(src.getUserId())
                .testDate(src.getTestDate())
                .dvRightEye(src.getDvRightEye())
                .dvLeftEye(src.getDvLeftEye())
                .nvRightEye(src.getNvRightEye())
                .nvLeftEye(src.getNvLeftEye())
                .imRightEye(src.getImRightEye())
                .imLeftEye(src.getImLeftEye())
                .frame(src.getFrame())
                .lens(src.getLens())
                .notes(src.getNotes())
                .bookingDate(src.getBookingDate())
                .deliveryDate(src.getDeliveryDate())
                // ✅ NO previousTests - clean copy
                .build();
    }
}