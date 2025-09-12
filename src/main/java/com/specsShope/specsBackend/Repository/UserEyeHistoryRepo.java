package com.specsShope.specsBackend.Repository;

import com.specsShope.specsBackend.Models.UserEyeHistory;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserEyeHistoryRepo extends MongoRepository<UserEyeHistory, String> {
    Optional<UserEyeHistory> findByUserId(String userId);
}