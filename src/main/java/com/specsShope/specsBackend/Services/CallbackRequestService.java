package com.specsShope.specsBackend.Services;

import com.specsShope.specsBackend.Dtos.CallbackRequestDTO;
import com.specsShope.specsBackend.Dtos.CallbackResponseDTO;
import com.specsShope.specsBackend.Dtos.CallbackStatsDTO;
import com.specsShope.specsBackend.Dtos.RevenueResponseDTO;

import java.util.List;
import java.util.Map;

public interface CallbackRequestService {
    CallbackResponseDTO submitRequest(String token, CallbackRequestDTO dto);

    List<CallbackResponseDTO> getAllRequests();

    List<CallbackResponseDTO> getRequestsByUserId(String userId);

    CallbackResponseDTO markAsCompleted(String id);

    CallbackStatsDTO getCallbackStats();

    void deleteRequest(String id);

    RevenueResponseDTO getRevenuePerMonth(Integer year);
    List<CallbackResponseDTO> getRecentCallbacks(int limit);

}