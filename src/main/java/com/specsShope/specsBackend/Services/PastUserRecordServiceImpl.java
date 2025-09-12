package com.specsShope.specsBackend.Services;

import com.specsShope.specsBackend.Dtos.*;
import com.specsShope.specsBackend.Mappers.EyeTestMapper;
import com.specsShope.specsBackend.Models.EyeMeasurement;
import com.specsShope.specsBackend.Models.EyeTestRecord;
import com.specsShope.specsBackend.Models.PastUserRecord;
import com.specsShope.specsBackend.Repository.PastUserRecordRepository;
import com.specsShope.specsBackend.Repository.EyeTestRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor
public class PastUserRecordServiceImpl implements PastUserRecordService {

    private final PastUserRecordRepository userRepo;
    private final EyeTestRepo eyeTestRepo;

    /* ---------- PAST-USER CRUD ---------- */
    @Override
    public PastUserRecordResponseDTO createPastUser(PastUserRecordRequestDTO dto) {
        if (userRepo.existsByPhone(dto.getPhone()))
            throw new RuntimeException("A user with this phone already exists");

        PastUserRecord user = PastUserRecord.builder()
                .name(dto.getName())
                .age(dto.getAge())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .address(dto.getAddress())
                .createdAt(LocalDateTime.now())
                .build();
        user.generatePublicId();
        return mapToDTO(userRepo.save(user));
    }

    @Override
    public PastUserRecordResponseDTO updatePastUser(String publicId, PastUserRecordRequestDTO dto) {
        PastUserRecord user = userRepo.findByPublicId(publicId)
                .orElseThrow(() -> new RuntimeException("Past user not found"));
        user.setName(dto.getName());
        user.setAge(dto.getAge());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setAddress(dto.getAddress());
        return mapToDTO(userRepo.save(user));
    }

    @Override
    public void deletePastUser(String publicId) {
        PastUserRecord user = userRepo.findByPublicId(publicId)
                .orElseThrow(() -> new RuntimeException("Past user not found"));
        userRepo.delete(user);
    }

    @Override
    public List<PastUserRecordResponseDTO> searchByName(String name) {
        return userRepo.findByNameContainingIgnoreCase(name)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public PastUserRecordResponseDTO getByPhone(String phone) {
        List<PastUserRecord> list = userRepo.findByPhone(phone);
        if (list.isEmpty()) throw new RuntimeException("Past user not found");
        if (list.size() > 1) throw new RuntimeException("Multiple users with this phone exist");
        return mapToDTO(list.get(0));
    }

    @Override
    public PastUserRecordResponseDTO getPastUserById(String publicId) {
        return mapToDTO(userRepo.findByPublicId(publicId)
                .orElseThrow(() -> new RuntimeException("Past user not found")));
    }

    /* ---------- EYE-TEST ---------- */
    @Override
    public EyeTestCreatedResponse addEyeTest(String publicId, EyeTestFormRequestDTO dto) {
        PastUserRecord user = userRepo.findByPublicId(publicId)
                .orElseThrow(() -> new RuntimeException("Past user not found"));

        EyeTestRecord entity = toEntity(dto, user.getId());
        entity.setCreatedAt(LocalDateTime.now());
        EyeTestRecord saved = eyeTestRepo.save(entity);

        user.getEyeTestHistory().add(saved);
        userRepo.save(user);

        return EyeTestCreatedResponse.builder()
                .testId(saved.getTestId())
                .publicId(user.getPublicId())
                .testDate(saved.getTestDate())
                .dvRightEye(toDto(saved.getDvRightEye()))
                .dvLeftEye(toDto(saved.getDvLeftEye()))
                .nvRightEye(toDto(saved.getNvRightEye()))
                .nvLeftEye(toDto(saved.getNvLeftEye()))
                .imRightEye(toDto(saved.getImRightEye()))
                .imLeftEye(toDto(saved.getImLeftEye()))
                .frame(saved.getFrame())
                .lens(saved.getLens())
                .notes(saved.getNotes())
                .bookingDate(saved.getBookingDate())
                .deliveryDate(saved.getDeliveryDate())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    @Override
    public EyeTestRecord updateEyeTest(String testId, EyeTestFormRequestDTO dto) {
        EyeTestRecord existing = eyeTestRepo.findById(testId)
                .orElseThrow(() -> new RuntimeException("Eye test not found"));

        // partial update
        if (dto.getTestDate() != null) existing.setTestDate(dto.getTestDate());
        if (dto.getDvRightEye() != null) existing.setDvRightEye(toEntity(dto.getDvRightEye()));
        if (dto.getDvLeftEye() != null) existing.setDvLeftEye(toEntity(dto.getDvLeftEye()));
        if (dto.getNvRightEye() != null) existing.setNvRightEye(toEntity(dto.getNvRightEye()));
        if (dto.getNvLeftEye() != null) existing.setNvLeftEye(toEntity(dto.getNvLeftEye()));
        if (dto.getImRightEye() != null) existing.setImRightEye(toEntity(dto.getImRightEye()));
        if (dto.getImLeftEye() != null) existing.setImLeftEye(toEntity(dto.getImLeftEye()));
        if (dto.getFrame() != null) existing.setFrame(dto.getFrame());
        if (dto.getLens() != null) existing.setLens(dto.getLens());
        if (dto.getNotes() != null) existing.setNotes(dto.getNotes());
        if (dto.getBookingDate() != null) existing.setBookingDate(dto.getBookingDate());
        if (dto.getDeliveryDate() != null) existing.setDeliveryDate(dto.getDeliveryDate());

        EyeTestRecord saved = eyeTestRepo.save(existing);

        // sync embedded copy inside user
        PastUserRecord user = userRepo.findById(saved.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.getEyeTestHistory().replaceAll(t -> t.getTestId().equals(saved.getTestId()) ? saved : t);
        userRepo.save(user);

        return saved;
    }

    @Override
    public void deleteEyeTest(String testId) {
        EyeTestRecord existing = eyeTestRepo.findById(testId)
                .orElseThrow(() -> new RuntimeException("Eye test not found"));

        // Delete from main repo
        eyeTestRepo.deleteById(testId);

        // Remove from user's embedded history
        PastUserRecord user = userRepo.findById(existing.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.getEyeTestHistory().removeIf(t -> t.getTestId().equals(testId));
        userRepo.save(user);
    }


    @Override
    public List<EyeTestRecord> getEyeTestsForUser(String publicId) {
        PastUserRecord user = userRepo.findByPublicId(publicId)
                .orElseThrow(() -> new RuntimeException("Past user not found"));
        return eyeTestRepo.findByUserIdOrderByTestDateDesc(user.getId());
    }

    /* ---------- helpers ---------- */
    private PastUserRecordResponseDTO mapToDTO(PastUserRecord u) {
        return PastUserRecordResponseDTO.builder()
                .publicId(u.getPublicId())
                .name(u.getName())
                .age(u.getAge())
                .phone(u.getPhone())
                .email(u.getEmail())
                .address(u.getAddress())
                .eyeTestHistory(u.getEyeTestHistory())
                .createdAt(u.getCreatedAt())
                .build();
    }

    private EyeTestRecord toEntity(EyeTestFormRequestDTO dto, String userId) {
        return EyeTestRecord.builder()
                .userId(userId)
                .testDate(dto.getTestDate())
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

    private EyeMeasurement toEntity(EyeMeasurementDTO d) {
        if (d == null) return null;
        return EyeMeasurement.builder()
                .sph(d.getSph()).cyl(d.getCyl()).axis(d.getAxis())
                .add(d.getAdd()).vision(d.getVision())
                .build();
    }

    private EyeMeasurementDTO toDto(EyeMeasurement e) {
        if (e == null) return null;
        return EyeMeasurementDTO.builder()
                .sph(e.getSph()).cyl(e.getCyl()).axis(e.getAxis())
                .add(e.getAdd()).vision(e.getVision())
                .build();
    }
    /* -------- satisfy interface signature -------- */
    @Override
    public EyeTestRecord updateEyeTest(String testId, EyeTestRecord partial) {
        // convert the partial entity to DTO and reuse the public method
        EyeTestFormRequestDTO dto = EyeTestFormRequestDTO.builder()
                .testDate(partial.getTestDate())
                .dvRightEye(toDto(partial.getDvRightEye()))
                .dvLeftEye(toDto(partial.getDvLeftEye()))
                .nvRightEye(toDto(partial.getNvRightEye()))
                .nvLeftEye(toDto(partial.getNvLeftEye()))
                .imRightEye(toDto(partial.getImRightEye()))
                .imLeftEye(toDto(partial.getImLeftEye()))
                .frame(partial.getFrame())
                .lens(partial.getLens())
                .notes(partial.getNotes())
                .bookingDate(partial.getBookingDate())
                .deliveryDate(partial.getDeliveryDate())
                .build();
        return updateEyeTest(testId, dto);   // calls your public DTO-based method
    }
    @Override
    public List<PastUserRecordResponseDTO> getAllPastUsers() {
        return userRepo.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

}