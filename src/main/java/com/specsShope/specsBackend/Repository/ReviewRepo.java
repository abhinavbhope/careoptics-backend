package com.specsShope.specsBackend.Repository;

import com.specsShope.specsBackend.Models.Review;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ReviewRepo extends MongoRepository<Review, String> {
    List<Review> findAllByProductIdOrderByCreatedAtDesc(String productId);
    List<Review> findAllByUserIdOrderByCreatedAtDesc(String userId);
    boolean existsByUserIdAndProductId(String userId, String productId);
    void deleteAllByProductId(String productId);   // admin/cascade use-case
}