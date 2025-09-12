package com.specsShope.specsBackend.Services;

import com.specsShope.specsBackend.Dtos.*;
import com.specsShope.specsBackend.Models.Review;
import com.specsShope.specsBackend.Repository.ProductRepo;
import com.specsShope.specsBackend.Repository.ReviewRepo;
import com.specsShope.specsBackend.Repository.UserRepo;
import com.specsShope.specsBackend.Services.ReviewService;
import com.specsShope.specsBackend.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepo reviewRepo;
    private final ModelMapper mapper;
    private final UserRepo userRepo;
    private final JwtService jwtService;
    private final ProductRepo productRepo;

    @Override
    public ReviewResponseDTO addReview(String rawToken, ReviewRequestDTO dto) {
        // Extract token and user info
        String token = rawToken.startsWith("Bearer ") ? rawToken.substring(7) : rawToken;
        String userId = jwtService.extractUserId(token);
        String username = jwtService.extractName(token);

        // Check if user already reviewed this product
        if (reviewRepo.existsByUserIdAndProductId(userId, dto.getProductId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Review already exists for this product");
        }

        // Create and save review
        Review review = Review.builder()
                .id(UUID.randomUUID().toString())
                .userId(userId)
                .username(username)
                .productId(dto.getProductId())
                .rating(dto.getRating())
                .comment(dto.getComment())
                .createdAt(Instant.now())
                .build();

        Review saved = reviewRepo.save(review);

        // Update product's average rating
        updateProductRatings(dto.getProductId());

        // Return properly mapped DTO with productName and createdAt
        return mapToDto(saved);
    }


    @Override
    public ReviewResponseDTO updateReview(String userId, String reviewId, ReviewRequestDTO dto) {
        Review r = reviewRepo.findById(reviewId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Review not found"));

        if (!r.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Not your review");
        }


        r.setRating(dto.getRating());
        r.setComment(dto.getComment());

        Review updated = reviewRepo.save(r);
        updateProductRatings(r.getProductId());
        return ReviewResponseDTO.builder()
                .id(updated.getId())
                .userId(updated.getUserId())
                .productId(updated.getProductId())
                .username(updated.getUsername())
                .rating(updated.getRating())
                .comment(updated.getComment())
                .createdAt(LocalDateTime.ofInstant(updated.getCreatedAt(), ZoneId.systemDefault()))
                .build();
    }


    @Override
    public void deleteReview(String userId, String reviewId) {
        Review r = reviewRepo.findById(reviewId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Review not found"));

        if (!r.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Not your review");
        }
        reviewRepo.delete(r);
        updateProductRatings(r.getProductId());
    }

    @Override
    public List<ReviewResponseDTO> getReviewsByProduct(String productId) {
        return reviewRepo.findAllByProductIdOrderByCreatedAtDesc(productId)
                .stream()
                .map(r -> {
                    String productName = productRepo.findById(r.getProductId())
                            .map(p -> p.getName()) // or p.getTitle()
                            .orElse("Unknown Product");

                    return ReviewResponseDTO.builder()
                            .id(r.getId())
                            .userId(r.getUserId())
                            .productId(r.getProductId())
                            .productName(productName)
                            .username(r.getUsername())
                            .rating(r.getRating())
                            .comment(r.getComment())
                            .createdAt(LocalDateTime.ofInstant(r.getCreatedAt(), ZoneId.systemDefault()))
                            .build();
                })
                .collect(Collectors.toList()); // ✅ needed to return List
    }


    @Override
    public List<ReviewResponseDTO> getReviewsByUser(String userId) {
        return reviewRepo.findAllByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(r -> {
                    String productName = productRepo.findById(r.getProductId())
                            .map(p -> p.getName())
                            .orElse("Unknown Product");

                    return ReviewResponseDTO.builder()
                            .id(r.getId())
                            .userId(r.getUserId())
                            .productId(r.getProductId())
                            .productName(productName)
                            .username(r.getUsername())
                            .rating(r.getRating())
                            .comment(r.getComment())
                            .createdAt(LocalDateTime.ofInstant(r.getCreatedAt(), ZoneId.systemDefault()))
                            .build();
                })
                .collect(Collectors.toList());
    }
    /* -------------------------------------------------
   ADMIN-SCOPED METHODS
   ------------------------------------------------- */

    @Override
    public List<ReviewResponseDTO> getAllReviews() {
        return reviewRepo.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ReviewResponseDTO getReviewById(String reviewId) {
        Review r = reviewRepo.findById(reviewId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Review not found"));
        return mapToDto(r);
    }

    public ReviewResponseDTO adminUpdateReview(String reviewId, AdminReviewUpdateDTO dto) {
        Review review = reviewRepo.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        if (dto.getProductId() != null) review.setProductId(dto.getProductId());
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());

        Review updated = reviewRepo.save(review);
        updateProductRatings(review.getProductId());

        return mapper.map(updated, ReviewResponseDTO.class);
    }


    @Override
    public void adminDeleteReview(String reviewId) {
        Review r = reviewRepo.findById(reviewId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found"));

        reviewRepo.delete(r);

        // Recalculate average rating
        updateProductRatings(r.getProductId());
    }

/* -------------------------------------------------
   PRIVATE HELPER
   ------------------------------------------------- */

    private ReviewResponseDTO mapToDto(Review r) {
        String productName = productRepo.findById(r.getProductId())
                .map(p -> p.getName())
                .orElse("Unknown Product");

        return ReviewResponseDTO.builder()
                .id(r.getId())
                .userId(r.getUserId())
                .productId(r.getProductId())
                .productName(productName)
                .username(r.getUsername())
                .rating(r.getRating())
                .comment(r.getComment())
                .createdAt(LocalDateTime.ofInstant(r.getCreatedAt(), ZoneId.systemDefault()))
                .build();
    }
    private void updateProductRatings(String productId) {
        List<Review> reviews = reviewRepo.findAllByProductIdOrderByCreatedAtDesc(productId);

        double avgRating = reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);

        List<String> reviewIds = reviews.stream()
                .map(Review::getId)
                .toList(); // always non-null

        productRepo.findById(productId).ifPresent(product -> {
            product.setAverageRating(avgRating);
            product.setReviewIds(reviewIds);          // never null
            productRepo.save(product);
        });
    }

}
