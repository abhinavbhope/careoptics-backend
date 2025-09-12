package com.specsShope.specsBackend.Dtos;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEyeTestHistoryDTO {
    private String userId;
    private String name;
    private String email;
    private String phone;
    private String address;
    private Integer age;

    @Builder.Default
    private List<EyeTestResponseDTO> eyeTestHistory = new ArrayList<>();
}
