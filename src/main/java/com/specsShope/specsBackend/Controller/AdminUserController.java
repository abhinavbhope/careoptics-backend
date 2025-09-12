package com.specsShope.specsBackend.Controller;

import com.specsShope.specsBackend.Dtos.*;
import com.specsShope.specsBackend.Models.User;
import com.specsShope.specsBackend.Repository.*;
import com.specsShope.specsBackend.Services.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final UserRepo userRepo;
    private final CartRepo cartRepo;
    private final AppointmentRepository appointmentRepository;
    private final CartServices cartServices;
    private final AppointmentService appointmentService;
    private final ReviewRepo reviewRepository;
    private final ModelMapper modelMapper;
    private final ProductRepo productRepo;

    // Get all users (admin only)
    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userRepo.findAll().stream()
                .map(user -> modelMapper.map(user, UserDto.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    // Get single user with complete profile
    @GetMapping("/{id}/details")
    public ResponseEntity<UserDetailsResponse> getUserDetails(@PathVariable String id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));

        UserDto userDto = modelMapper.map(user, UserDto.class);
        CartDTO cart = cartServices.getCartByUserId(id);
        List<AppointmentResponseDTO> appointments = appointmentService.getUserAppointments(id);
        List<ReviewResponseDTO> reviews = reviewRepository.findAllByUserIdOrderByCreatedAtDesc(id)
                .stream()
                .map(review -> {
                    String productName = productRepo.findById(review.getProductId())
                            .map(product -> product.getName())
                            .orElse("Unknown Product");

                    return ReviewResponseDTO.builder()
                            .id(review.getId())
                            .userId(review.getUserId())
                            .productId(review.getProductId())
                            .productName(productName) // ✅ Now populated
                            .username(review.getUsername())
                            .rating(review.getRating())
                            .comment(review.getComment())
                            .createdAt(LocalDateTime.ofInstant(review.getCreatedAt(), ZoneId.systemDefault()))
                            .build();
                })
                .collect(Collectors.toList());

        UserDetailsResponse response = new UserDetailsResponse(userDto, cart, appointments, reviews);
        return ResponseEntity.ok(response);
    }

    // Delete user and all associated data
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable String id) {
        if (!userRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        // Cascade delete all user data
        cartRepo.findByUserId(id).ifPresent(cartRepo::delete);
        appointmentRepository.findByUserId(id).forEach(appointmentRepository::delete);
        reviewRepository.findAllByUserIdOrderByCreatedAtDesc(id).forEach(reviewRepository::delete);
        userRepo.deleteById(id);

        return ResponseEntity.ok("User and all associated data deleted successfully");
    }
}