package com.specsShope.specsBackend.Controller;

import com.specsShope.specsBackend.Dtos.CallbackRequestDTO;
import com.specsShope.specsBackend.Dtos.CallbackResponseDTO;
import com.specsShope.specsBackend.Services.CallbackRequestService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/callbacks")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class CallbackRequestController {

    private final CallbackRequestService callbackRequestService;

    @PostMapping("/request")
    public ResponseEntity<CallbackResponseDTO> submitCallbackRequest(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody @Valid CallbackRequestDTO requestDTO
    ) {
        // Validate and extract JWT
        if (authHeader == null || authHeader.isBlank()) {
            return ResponseEntity.status(401).build(); // Unauthorized
        }

        String jwt;
        if (authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7).trim();
            if (jwt.isEmpty()) {
                return ResponseEntity.status(401).build();
            }
        } else {
            jwt = authHeader.trim();
            if (jwt.isEmpty()) {
                return ResponseEntity.status(401).build();
            }
        }

        // Delegate to service
        CallbackResponseDTO response = callbackRequestService.submitRequest(jwt, requestDTO);
        return ResponseEntity.ok(response);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<CallbackResponseDTO>> getAllCallbacks() {
        List<CallbackResponseDTO> callbacks = callbackRequestService.getAllRequests();
        return ResponseEntity.ok(callbacks);
    }

}
