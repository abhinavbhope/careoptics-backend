package com.specsShope.specsBackend.Dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UserDetailsResponse {
    private UserDto user;
    private CartDTO cart;
    private List<AppointmentResponseDTO> appointments;
    private List<ReviewResponseDTO> reviews;
}
