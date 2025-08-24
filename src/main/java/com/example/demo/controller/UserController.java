package com.example.demo.controller;
import com.example.demo.dto.UserCreateDto;
import com.example.demo.dto.UserDto;
import com.example.demo.dto.UserUpdateDto;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;

import lombok.*;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;

    @PostMapping("/")
    public UserDto createUser(
            @RequestBody UserCreateDto createdUser
    ) {
        return userService.createUser(createdUser);
    }

    @GetMapping("/{id}")
    public UserDto getUserById(
            @PathVariable long id
    ) {
        return userService.getUserById(id);
    }

    @GetMapping("/")
    public List<UserDto> getAllUsers(
    ) {
        return userService.getAllUsers();
    }

    @PutMapping("/")
    public UserDto updateUser(
            @RequestBody UserUpdateDto updatedUser
    ) {
        return userService.updateUser(updatedUser);
    }

    @DeleteMapping("/")
    public boolean deleteUser(
            Long id
    ) {
        return userService.delete(id);
    }
}

