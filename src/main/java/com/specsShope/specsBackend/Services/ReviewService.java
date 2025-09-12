package com.specsShope.specsBackend.Services;

import com.specsShope.specsBackend.Dtos.*;

import java.util.List;

public interface ReviewService {
    /* user-scoped methods */
    ReviewResponseDTO addReview(String authHeader, ReviewRequestDTO dto);
    ReviewResponseDTO updateReview(String username, String reviewId, ReviewRequestDTO dto);
    void deleteReview(String userId, String reviewId);
    List<ReviewResponseDTO> getReviewsByProduct(String productId);
    List<ReviewResponseDTO> getReviewsByUser(String userId);

    /* admin-scoped methods */
    List<ReviewResponseDTO> getAllReviews();
    ReviewResponseDTO getReviewById(String reviewId);
    ReviewResponseDTO adminUpdateReview(String reviewId, AdminReviewUpdateDTO dto);  // <- updated
    void adminDeleteReview(String reviewId);
}
