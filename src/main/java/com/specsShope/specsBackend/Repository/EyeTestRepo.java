package com.specsShope.specsBackend.Repository;

import com.specsShope.specsBackend.Models.EyeTestRecord;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EyeTestRepo extends MongoRepository<EyeTestRecord, String> {

    // All tests for a user, latest first
    Optional<EyeTestRecord> findByUserId(String userId);
    List<EyeTestRecord> findByUserIdOrderByTestDateDesc(String userId);
    void deleteAllByUserId(String userId);
}
