package com.specsShope.specsBackend.Models;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.UUID;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "past_users")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class PastUserRecord {
    @Id private String id;
    private String publicId;
    private String name;
    private String email;
    private String phone;
    private Integer age;
    private String address;
    @Builder.Default
    private List<EyeTestRecord> eyeTestHistory = new ArrayList<>();
    @CreatedDate private LocalDateTime createdAt;

    public void generatePublicId() {
        if (publicId == null || publicId.isBlank()) {
            publicId = UUID.randomUUID().toString();
        }
    }
}