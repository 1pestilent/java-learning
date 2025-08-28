package com.example.demo.controller;
import com.example.demo.dto.UserCreateDto;
import com.example.demo.dto.UserDto;
import com.example.demo.dto.UserUpdateDto;
import com.example.demo.service.UserServiceImpl;
import lombok.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserServiceImpl userService ;

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
    public void deleteUser(
            Long id
    ) {
        userService.deleteUser(id);
    }
}

