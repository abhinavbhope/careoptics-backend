package com.specsShope.specsBackend.Dtos;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private String id;
    private String name;
    private String email;
    private String username;
    private String phone;
    private Integer age;
    private String address;
    private String role;
    private String createdAt;
}