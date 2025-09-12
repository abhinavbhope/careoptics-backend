package com.specsShope.specsBackend.Models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "user_eye_history")
public class UserEyeHistory {
    @Id
    private String id;          // = userId
    private String userId;
    @Builder.Default
    private List<EyeTestRecord> tests = new ArrayList<>();
}