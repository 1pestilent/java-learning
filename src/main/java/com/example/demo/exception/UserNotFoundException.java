package com.example.demo.exception;

import lombok.Getter;

@Getter
public class UserNotFoundException extends RuntimeException {
    private final Long id;
    public final String message;

    public UserNotFoundException(Long id)
    {
        super("User with id '" + id + "' doesnt exist!`");

        this.id = id;
        this.message = super.getMessage();
    }
}
