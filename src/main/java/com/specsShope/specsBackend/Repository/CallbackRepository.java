package com.specsShope.specsBackend.Repository;


import com.specsShope.specsBackend.Models.CallbackRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface CallbackRepository extends MongoRepository<CallbackRequest, String> {
    Optional<CallbackRequest> findByUserId(String userId);
    List<CallbackRequest> findByCompletedTrue();
    List<CallbackRequest> findByCompletedTrueOrderByRequestedAtDesc(Pageable pageable);


}
