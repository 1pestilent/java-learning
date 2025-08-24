package com.example.demo.dto;
import lombok.Data;

@Data
public class UserCreateDto {
    private String username;
    private String email;
    private int age;

    public UserCreateDto(String username, String email, int age) {
        this.username = username;
        this.email = email;
        this.age = age;
    }
}
