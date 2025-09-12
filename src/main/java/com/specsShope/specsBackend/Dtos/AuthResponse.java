package com.specsShope.specsBackend.Dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;      // JWT token
    private String role;       // USER / ADMIN
    private String userId;     // Database ID
    private String username;   // User's name
    private String address;    // User's address
    private Integer age;       // User's age
}
