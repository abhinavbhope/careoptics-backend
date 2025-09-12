package com.specsShope.specsBackend.Dtos;

import com.specsShope.specsBackend.Models.EyeTestRecord;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PastUserRecordResponseDTO {
    private String publicId;
    private String name;
    private int age;
    private String email;
    private String phone;
    private String address;
    private List<EyeTestRecord> eyeTestHistory;
    private LocalDateTime createdAt;
}
