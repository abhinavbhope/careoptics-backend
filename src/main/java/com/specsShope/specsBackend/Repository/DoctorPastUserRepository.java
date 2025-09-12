package com.specsShope.specsBackend.Repository;

import com.specsShope.specsBackend.Models.DocAppointment;
import com.specsShope.specsBackend.Models.DoctorPastUser;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorPastUserRepository extends MongoRepository<DoctorPastUser, String> {
    Optional<DoctorPastUser> findByPhone(String phone);
    boolean existsByPhone(String phone);
    // DocAppointmentRepository  (drop-in)
      // Spring Data built-in
    Optional<DoctorPastUser> findByNameIgnoreCase(String name);
    List<DoctorPastUser> findByPhoneContainingIgnoreCaseOrNameContainingIgnoreCase(String phone, String name);


}