package com.specsShope.specsBackend.Controller;

import com.specsShope.specsBackend.Dtos.*;
import com.specsShope.specsBackend.Models.CustomUserDetails;
import com.specsShope.specsBackend.Services.ReviewService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
@Validated
public class ReviewController {

    private final ReviewService reviewService;

    /* ---- USER-SCOPED ENDPOINTS ---- */

    @PostMapping
    public ResponseEntity<ReviewResponseDTO> add(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody ReviewRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reviewService.addReview(authHeader, dto));
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewResponseDTO> update(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable String reviewId,
            @Valid @RequestBody ReviewRequestDTO dto) {
        return ResponseEntity.ok(
                reviewService.updateReview(user.getUsername(), reviewId, dto));
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String reviewId) {
        reviewService.deleteReview(userDetails.getUserId(), reviewId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my")
    public ResponseEntity<List<ReviewResponseDTO>> getMyReviews(
            @AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(reviewService.getReviewsByUser(user.getUserId()));
    }

    /* ---- PUBLIC / PRODUCT-SCOPED ENDPOINT ---- */

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ReviewResponseDTO>> getByProduct(
            @PathVariable String productId) {
        return ResponseEntity.ok(reviewService.getReviewsByProduct(productId));
    }

    /* ---- ADMIN-SCOPED ENDPOINTS ---- */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/all")
    public ResponseEntity<List<ReviewResponseDTO>> adminGetAllReviews() {
        return ResponseEntity.ok(reviewService.getAllReviews());
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/{reviewId}")
    public ResponseEntity<ReviewResponseDTO> adminGetReviewById(@PathVariable String reviewId) {
        return ResponseEntity.ok(reviewService.getReviewById(reviewId));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/{reviewId}")
    public ResponseEntity<ReviewResponseDTO> adminUpdateReview(
            @PathVariable String reviewId,
            @Valid @RequestBody AdminReviewUpdateDTO dto) {
        return ResponseEntity.ok(reviewService.adminUpdateReview(reviewId, dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/{reviewId}")
    public ResponseEntity<Void> adminDeleteReview(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String reviewId) {

        System.out.println("✅ Inside Controller: " + userDetails.getUsername());
        System.out.println("✅ Authorities in Controller: " + userDetails.getAuthorities());

        reviewService.adminDeleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }


}