package com.specsShope.specsBackend.Controller;

import com.specsShope.specsBackend.Dtos.CallbackResponseDTO;
import com.specsShope.specsBackend.Dtos.CallbackStatsDTO;
import com.specsShope.specsBackend.Dtos.RevenueResponseDTO;
import com.specsShope.specsBackend.Services.CallbackRequestService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/callbacks")
@AllArgsConstructor
public class CallbackAdminController {

    private final CallbackRequestService callbackRequestService;

    // 1️⃣ Get all callback requests
    @GetMapping
    public ResponseEntity<List<CallbackResponseDTO>> getAllRequests() {
        List<CallbackResponseDTO> requests = callbackRequestService.getAllRequests();
        return ResponseEntity.ok(requests);
    }

    // 2️⃣ Get callback requests by user ID
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CallbackResponseDTO>> getRequestsByUser(@PathVariable String userId) {
        List<CallbackResponseDTO> requests = callbackRequestService.getRequestsByUserId(userId);
        return ResponseEntity.ok(requests);
    }

    // 3️⃣ Mark a callback request as completed
    @PatchMapping("/{id}/complete")
    public ResponseEntity<CallbackResponseDTO> markCompleted(@PathVariable String id) {
        CallbackResponseDTO updated = callbackRequestService.markAsCompleted(id);
        return ResponseEntity.ok(updated);
    }

    // 4️⃣ Delete a callback request
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRequest(@PathVariable String id) {
        callbackRequestService.deleteRequest(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/stats")
    public ResponseEntity<CallbackStatsDTO> getCallbackStats() {
        CallbackStatsDTO stats = callbackRequestService.getCallbackStats();
        return ResponseEntity.ok(stats);
    }
    @GetMapping("/revenue")
    public ResponseEntity<RevenueResponseDTO> getRevenueStats(
            @RequestParam(value = "year", required = false) Integer year) {
        RevenueResponseDTO response = callbackRequestService.getRevenuePerMonth(year);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/recent")
    public ResponseEntity<List<CallbackResponseDTO>> getRecentCallbacks(
            @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(callbackRequestService.getRecentCallbacks(limit));
    }


}
