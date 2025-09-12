package com.specsShope.specsBackend.Services;

import com.specsShope.specsBackend.Dtos.DoctorAppointmentRequest;
import com.specsShope.specsBackend.Dtos.DoctorAppointmentResponse;
import com.specsShope.specsBackend.Dtos.UserResponse;
import com.specsShope.specsBackend.Models.DocAppointment;
import com.specsShope.specsBackend.Models.DoctorPastUser;
import com.specsShope.specsBackend.Models.User;
import com.specsShope.specsBackend.Repository.DocAppointmentRepository;
import com.specsShope.specsBackend.Repository.UserRepo;
import com.specsShope.specsBackend.exception.NotFoundException;
import com.specsShope.specsBackend.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class DoctorAppointmentServiceImpl implements DoctorAppointmentService {

    private final DocAppointmentRepository repo;
    private final UserRepo userRepo;
    private final MongoTemplate mongoTemplate;
    private final EmailService emailService;

    @Value("${spring.mail.username}")
    private String adminEmail;

    /* -------------------- ADMIN / USER CREATE -------------------- */
    @Override
    @Transactional
    public DoctorAppointmentResponse create(DoctorAppointmentRequest dto, String creatorId, boolean isAdmin) {
        if (dto.getUserId() != null && !userRepo.existsById(dto.getUserId()))
            throw new NotFoundException("Target user not found");

        DocAppointment appt = DocAppointment.builder()
                .patientName(dto.getPatientName())
                .age(dto.getAge())
                .phone(dto.getPhone())
                .address(dto.getAddress())
                .reasonForVisit(dto.getReasonForVisit())
                .appointmentDate(dto.getAppointmentDate())
                .userId(dto.getUserId())
                .build();

        appt = repo.save(appt);

        if (appt.getUserId() != null) {
            mongoTemplate.updateFirst(
                    Query.query(Criteria.where("id").is(appt.getUserId())),
                    new Update().addToSet("doctorAppointmentIds", appt.getId()),
                    User.class);
        }

        // --- e-mails omitted for brevity (same as before) ---
        return map(appt);
    }

    /* -------------------- UPDATE -------------------- */
    @Override
    @Transactional
    public DoctorAppointmentResponse update(String id, DoctorAppointmentRequest dto,
                                            String currentUserId, boolean isAdmin) {
        DocAppointment existing = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Appointment not found"));

        if (!isAdmin && !existing.getUserId().equals(currentUserId))
            throw new UnauthorizedException("Access denied");

        existing.setPatientName(dto.getPatientName());
        existing.setAge(dto.getAge());
        existing.setPhone(dto.getPhone());
        existing.setAddress(dto.getAddress());
        existing.setReasonForVisit(dto.getReasonForVisit());
        existing.setAppointmentDate(dto.getAppointmentDate());

        return map(repo.save(existing));
    }

    /* -------------------- DELETE -------------------- */
    @Override
    @Transactional
    public void delete(String id, String currentUserId, boolean isAdmin) {
        DocAppointment appt = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Appointment not found"));

        if (!isAdmin && (appt.getUserId() != null && !appt.getUserId().equals(currentUserId)))
            throw new UnauthorizedException("Access denied");

        repo.deleteById(id);

        if (appt.getUserId() != null) {
            mongoTemplate.updateFirst(
                    Query.query(Criteria.where("id").is(appt.getUserId())),
                    new Update().pull("doctorAppointmentIds", id),
                    User.class);
        }
        if (appt.getPastUserId() != null) {
            mongoTemplate.updateFirst(
                    Query.query(Criteria.where("id").is(appt.getPastUserId())),
                    new Update().pull("doctorAppointmentIds", id),
                    DoctorPastUser.class);
        }
    }

    /* -------------------- READ -------------------- */
    @Override
    public DoctorAppointmentResponse getById(String id, String currentUserId, boolean isAdmin) {
        DocAppointment appt = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Appointment not found"));
        if (!isAdmin && !appt.getUserId().equals(currentUserId))
            throw new UnauthorizedException("Access denied");
        return map(appt);
    }

    @Override
    public List<DoctorAppointmentResponse> getAllForUser(String userId) {
        return repo.findByUserIdOrderByAppointmentDateDesc(userId)
                .stream().map(this::map).collect(Collectors.toList());
    }

    /* -------------------- SEARCH / UNIFIED -------------------- */
    @Override
    public List<DoctorAppointmentResponse> searchByPhone(String phone) {
        return repo.findByPhoneOrderByAppointmentDateDesc(phone)
                .stream().map(this::map).toList();
    }

    public List<DoctorAppointmentResponse> getUnifiedMyHistory(String userId, String phone) {
        List<DocAppointment> registered = repo.findByUserIdOrderByAppointmentDateDesc(userId);
        List<DocAppointment> walkIns    = repo.findByPhoneAndUserIdNull(phone);
        return Stream.concat(registered.stream(), walkIns.stream())
                .sorted(Comparator.comparing(DocAppointment::getAppointmentDate).reversed())
                .map(this::map)
                .toList();
    }

    @Override
    public List<UserResponse> searchRegisteredUsers(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return userRepo.findAll().stream()
                    .map(this::mapUserWithAppointments)
                    .toList();
        }
        return userRepo.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(keyword, keyword)
                .stream()
                .map(this::mapUserWithAppointments)
                .toList();
    }


    private UserResponse mapUserWithAppointments(User user) {
        List<DoctorAppointmentResponse> appts = getAllForUser(user.getId());
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole().name())
                .appointments(appts)
                .build();
    }

    private DoctorAppointmentResponse map(DocAppointment a) {
        return DoctorAppointmentResponse.builder()
                .id(a.getId())
                .patientName(a.getPatientName())
                .age(a.getAge())
                .phone(a.getPhone())
                .address(a.getAddress())
                .reasonForVisit(a.getReasonForVisit())
                .appointmentDate(a.getAppointmentDate())
                .createdAt(a.getCreatedAt())
                .userId(a.getUserId())
                .build();
    }
}