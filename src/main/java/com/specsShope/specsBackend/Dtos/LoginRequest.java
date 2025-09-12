package com.specsShope.specsBackend.Dtos;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}
