package com.example.demo.exception;

import lombok.Getter;

@Getter
public class UserAlreadyExistsException extends RuntimeException{
    private final String email;
    private final String username;
    private final String message;

    public UserAlreadyExistsException(String message, String email, String username) {
        super(message);
        this.email = email;
        this.username = username;
        this.message = super.getMessage();
    }
}
