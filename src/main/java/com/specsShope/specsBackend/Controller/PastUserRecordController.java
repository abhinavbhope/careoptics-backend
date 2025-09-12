package com.specsShope.specsBackend.Controller;

import com.specsShope.specsBackend.Dtos.EyeTestCreatedResponse;
import com.specsShope.specsBackend.Dtos.EyeTestFormRequestDTO;
import com.specsShope.specsBackend.Dtos.PastUserRecordRequestDTO;
import com.specsShope.specsBackend.Dtos.PastUserRecordResponseDTO;
import com.specsShope.specsBackend.Mappers.EyeTestMapper;
import com.specsShope.specsBackend.Models.EyeTestRecord;
import com.specsShope.specsBackend.Services.PastUserRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/past-users")
@RequiredArgsConstructor
public class PastUserRecordController {

    private final PastUserRecordService service;

    // ✅ Create a past user record
    @PostMapping
    public ResponseEntity<PastUserRecordResponseDTO> createPastUser(@RequestBody PastUserRecordRequestDTO dto) {
        return ResponseEntity.ok(service.createPastUser(dto));
    }

    @PutMapping("/{publicId}")
    public ResponseEntity<PastUserRecordResponseDTO> updatePastUser(
            @PathVariable String publicId,   // consistent now
            @RequestBody PastUserRecordRequestDTO dto
    ) {
        return ResponseEntity.ok(service.updatePastUser(publicId, dto));
    }

    @DeleteMapping("/{publicId}")
    public ResponseEntity<Void> deletePastUser(@PathVariable String publicId) {
        service.deletePastUser(publicId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{publicId}")
    public ResponseEntity<PastUserRecordResponseDTO> getPastUserById(@PathVariable String publicId) {
        return ResponseEntity.ok(service.getPastUserById(publicId));
    }

    @PostMapping("/{publicId}/eye-tests")
    public ResponseEntity<EyeTestCreatedResponse> addEyeTest(
            @PathVariable String publicId,
            @RequestBody @Valid EyeTestFormRequestDTO dto
    ) {
        return ResponseEntity.ok(service.addEyeTest(publicId, dto));
    }
    @GetMapping("/search")
    public ResponseEntity<List<PastUserRecordResponseDTO>> searchUsers(@RequestParam String name) {
        return ResponseEntity.ok(service.searchByName(name));
    }

    @GetMapping("/by-phone")
    public ResponseEntity<PastUserRecordResponseDTO> getByPhone(@RequestParam String phone) {
        return ResponseEntity.ok(service.getByPhone(phone));
    }

    // ✅ Get all
    @GetMapping
    public ResponseEntity<List<PastUserRecordResponseDTO>> getAllPastUsers() {
        return ResponseEntity.ok(service.getAllPastUsers());
    }
    // ✅ Update an eye test
    @PutMapping("/eye-tests/{testId}")
    public ResponseEntity<EyeTestRecord> updateEyeTest(
            @PathVariable String testId,
            @RequestBody @Valid EyeTestFormRequestDTO dto
    ) {
        return ResponseEntity.ok(service.updateEyeTest(testId, dto));
    }

    // ✅ Delete an eye test
    @DeleteMapping("/eye-tests/{testId}")
    public ResponseEntity<Void> deleteEyeTest(@PathVariable String testId) {
        service.deleteEyeTest(testId);
        return ResponseEntity.noContent().build();
    }

    // ✅ Get all eye tests for a user
    @GetMapping("/{publicId}/eye-tests")
    public ResponseEntity<List<EyeTestRecord>> getEyeTestsForUser(@PathVariable String publicId) {
        return ResponseEntity.ok(service.getEyeTestsForUser(publicId));
    }


}
