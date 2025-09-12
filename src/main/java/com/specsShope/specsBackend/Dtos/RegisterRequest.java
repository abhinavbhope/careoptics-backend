package com.specsShope.specsBackend.Dtos;

import com.specsShope.specsBackend.Models.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    private String email;
    private String password;
    private String phone;
    private String name;
    private String otp;
    private Role role;
    private Integer age;
    private String address;
    private String username;
}
