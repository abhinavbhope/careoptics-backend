package com.specsShope.specsBackend.Services;

import com.specsShope.specsBackend.Dtos.EyeTestCreatedResponse;
import com.specsShope.specsBackend.Dtos.EyeTestFormRequestDTO;
import com.specsShope.specsBackend.Dtos.PastUserRecordRequestDTO;
import com.specsShope.specsBackend.Dtos.PastUserRecordResponseDTO;
import com.specsShope.specsBackend.Models.EyeTestRecord;

import java.util.List;

public interface PastUserRecordService {

    // ---------- Past User CRUD ----------
    PastUserRecordResponseDTO createPastUser(PastUserRecordRequestDTO requestDTO);

    PastUserRecordResponseDTO updatePastUser(String id, PastUserRecordRequestDTO requestDTO);

    void deletePastUser(String id);

    List<PastUserRecordResponseDTO> searchByName(String name);

    PastUserRecordResponseDTO getByPhone(String phone);

    PastUserRecordResponseDTO getPastUserById(String id);
    List<PastUserRecordResponseDTO> getAllPastUsers();
    // ---------- Eye Test (separate collection) ----------
    EyeTestCreatedResponse addEyeTest(String pastUserId, EyeTestFormRequestDTO dto);    EyeTestRecord updateEyeTest(String testId, EyeTestRecord record);

    void deleteEyeTest(String testId);

    List<EyeTestRecord> getEyeTestsForUser(String userId);
    // public API – accepts DTO
    EyeTestRecord updateEyeTest(String testId, EyeTestFormRequestDTO dto);
}
