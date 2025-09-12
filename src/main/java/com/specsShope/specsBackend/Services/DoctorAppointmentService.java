package com.specsShope.specsBackend.Services;

import com.specsShope.specsBackend.Dtos.DoctorAppointmentRequest;
import com.specsShope.specsBackend.Dtos.DoctorAppointmentResponse;
import com.specsShope.specsBackend.Dtos.UserResponse;
import com.specsShope.specsBackend.Models.DoctorPastUser;
import com.specsShope.specsBackend.Models.User;

import java.util.List;

public interface DoctorAppointmentService {

    DoctorAppointmentResponse create(DoctorAppointmentRequest dto, String creatorId, boolean isAdmin);

    DoctorAppointmentResponse update(String id, DoctorAppointmentRequest dto, String currentUserId, boolean isAdmin);

    void delete(String id, String currentUserId, boolean isAdmin);

    DoctorAppointmentResponse getById(String id, String currentUserId, boolean isAdmin);

    List<DoctorAppointmentResponse> getAllForUser(String userId);

    /* ---- NEW ---- */
    List<DoctorAppointmentResponse> searchByPhone(String phone);
    /* ---- NEW ---- */
// DoctorAppointmentService.java
    List<UserResponse> searchRegisteredUsers(String keyword);


}