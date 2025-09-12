package com.specsShope.specsBackend.Repository;

import com.specsShope.specsBackend.Models.PastUserRecord;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface PastUserRecordRepository extends MongoRepository<PastUserRecord, String> {

    List<PastUserRecord> findByNameContainingIgnoreCase(String name);

    List<PastUserRecord> findByPhone(String phone);
    Optional<PastUserRecord> findByPublicId(String publicId);

    boolean existsByPhone(String phone);

    boolean existsByPublicId(String publicId);
}