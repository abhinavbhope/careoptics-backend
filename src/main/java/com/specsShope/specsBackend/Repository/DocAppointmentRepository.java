package com.specsShope.specsBackend.Repository;

import com.specsShope.specsBackend.Models.DocAppointment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocAppointmentRepository extends MongoRepository<DocAppointment, String> {

    /* ========== USER-SPECIFIC ========== */
    List<DocAppointment> findByUserIdOrderByAppointmentDateDesc(String userId);

    boolean existsByUserId(String userId);

    /* ownership check for USER role */
    @Query("{ '_id': ?0, 'userId': ?1 }")
    Optional<DocAppointment> findByIdAndUserId(String id, String userId);

    /* ========== ADMIN-SPECIFIC ========== */
    /* nothing extra – save(), findById(), deleteById() are inherited */
    List<DocAppointment> findByPhoneOrderByAppointmentDateDesc(String phone);
    /* ---------- ADD THIS LINE ---------- */
    List<DocAppointment> findByPhoneAndUserIdNull(String phone);
    List<DocAppointment> findAllByIdIn(List<String> ids);

}