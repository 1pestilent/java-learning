package com.example.demo.dto;

import lombok.Data;

@Data
public class UserUpdateDto {
    private long id;
    private String username;
    private String email;
    private String age;
}