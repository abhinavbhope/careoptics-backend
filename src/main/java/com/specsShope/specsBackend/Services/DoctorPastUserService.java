package com.specsShope.specsBackend.Services;

import com.specsShope.specsBackend.Dtos.*;
import com.specsShope.specsBackend.Models.DocAppointment;
import com.specsShope.specsBackend.Models.DoctorPastUser;
import com.specsShope.specsBackend.Repository.DocAppointmentRepository;
import com.specsShope.specsBackend.Repository.DoctorPastUserRepository;
import com.specsShope.specsBackend.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorPastUserService {

    private final DoctorPastUserRepository pastUserRepo;
    private final DocAppointmentRepository appointmentRepo;
    private final MongoTemplate mongoTemplate;

    /* ---------- 1. create / update patient ---------- */
    @Transactional
    public DoctorPastUserResponse createPatient(DoctorPastUserRequest dto) {
        // Check if a past user with the same phone already exists
        boolean exists = pastUserRepo.existsByPhone(dto.getPhone());
        if (exists) {
            throw new IllegalArgumentException("Past user with this phone number already exists");
        }

        DoctorPastUser user = DoctorPastUser.builder()
                .name(dto.getName())
                .age(dto.getAge())
                .phone(dto.getPhone())
                .address(dto.getAddress())
                .build();
        return map(pastUserRepo.save(user));
    }



    public List<DoctorPastUserResponse> listAll() {
        return pastUserRepo.findAll().stream().map(this::map).collect(Collectors.toList());
    }

    /* ---------- 2. append appointment ---------- */
    @Transactional
    public DoctorAppointmentResponse addAppointment(String pastUserId,
                                                    AddAppointmentToPastUserRequest dto) {
        DoctorPastUser patient = pastUserRepo.findById(pastUserId)
                .orElseThrow(() -> new NotFoundException("Past patient not found"));

        DocAppointment appt = DocAppointment.builder()
                .patientName(patient.getName())
                .age(patient.getAge())
                .phone(patient.getPhone())
                .address(patient.getAddress())
                .reasonForVisit(dto.getReasonForVisit())
                .appointmentDate(dto.getAppointmentDate())
                .userId(null)                       // not a registered user
                .pastUserId(pastUserId)             // NEW field (see below)
                .build();

        appt = appointmentRepo.save(appt);

        // push id into patient's list

        mongoTemplate.updateFirst(
                Query.query(Criteria.where("id").is(pastUserId)),
                new Update().addToSet("doctorAppointmentIds", appt.getId()),
                DoctorPastUser.class);

        return mapAppointment(appt);
    }

    /* ---------- mappers ---------- */
    private DoctorPastUserResponse map(DoctorPastUser u) {
        return DoctorPastUserResponse.builder()
                .id(u.getId())
                .name(u.getName())
                .age(u.getAge())
                .phone(u.getPhone())
                .address(u.getAddress())
                .doctorAppointmentIds(u.getDoctorAppointmentIds())
                .createdAt(u.getCreatedAt())
                .build();
    }

    private DoctorAppointmentResponse mapAppointment(DocAppointment a) {
        return DoctorAppointmentResponse.builder()
                .id(a.getId())
                .patientName(a.getPatientName())
                .age(a.getAge())
                .phone(a.getPhone())
                .address(a.getAddress())
                .reasonForVisit(a.getReasonForVisit())
                .appointmentDate(a.getAppointmentDate())
                .createdAt(a.getCreatedAt())
                .userId(a.getUserId())        // null for past users
                .build();
    }
    /* ---------- 3. fetch all appointments of a past patient ---------- */
    public List<DoctorAppointmentResponse> getAppointmentsByPastUser(String pastUserId) {
        // we stored the ids; fetch them newest-first
        DoctorPastUser patient = pastUserRepo.findById(pastUserId)
                .orElseThrow(() -> new NotFoundException("Past patient not found"));

        if (patient.getDoctorAppointmentIds().isEmpty()) {
            return List.of();
        }

        // MongoDB _id IN ( …ids… )  newest first
        List<DocAppointment> docs = appointmentRepo.findAllById(patient.getDoctorAppointmentIds())
                .stream()
                .sorted(Comparator.comparing(DocAppointment::getAppointmentDate).reversed())
                .toList();

        return docs.stream().map(this::mapAppointment).toList();
    }
    @Transactional
    public DoctorPastUserResponse updatePatient(String pastUserId, DoctorPastUserRequest dto) {
        DoctorPastUser patient = pastUserRepo.findById(pastUserId)
                .orElseThrow(() -> new NotFoundException("Past patient not found"));

        patient.setName(dto.getName());
        patient.setAge(dto.getAge());
        patient.setPhone(dto.getPhone());
        patient.setAddress(dto.getAddress());

        return map(pastUserRepo.save(patient));
    }
    @Transactional
    public void deletePastUser(String pastUserId) {
        // Optional: also delete all linked appointments
        DoctorPastUser patient = pastUserRepo.findById(pastUserId)
                .orElseThrow(() -> new NotFoundException("Past patient not found"));

        if (!patient.getDoctorAppointmentIds().isEmpty()) {
            appointmentRepo.deleteAllById(patient.getDoctorAppointmentIds());
        }

        pastUserRepo.deleteById(pastUserId);
    }
    /* ---------- 4. search past users ---------- */
    public List<DoctorPastUserResponse> searchPastUsers(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return List.of();
        }

        // Try phone exact match first
        Optional<DoctorPastUser> byPhone = pastUserRepo.findByPhone(keyword);
        if (byPhone.isPresent()) {
            return List.of(map(byPhone.get()));
        }

        // Otherwise search by partial name
        return pastUserRepo.findAll().stream()
                .filter(u -> u.getName() != null &&
                        u.getName().toLowerCase().contains(keyword.toLowerCase()))
                .map(this::map)
                .toList();
    }




}