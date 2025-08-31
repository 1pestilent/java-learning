package com.example.demo.controller;
import com.example.demo.dto.UserCreateDto;
import com.example.demo.dto.UserDto;
import com.example.demo.dto.UserUpdateDto;
import com.example.demo.service.UserServiceImpl;
import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserServiceImpl userService ;

    @PostMapping("/")
    public ResponseEntity<UserDto> createUser(
            @RequestBody UserCreateDto createdUser
    ) {
        UserDto userDto = userService.createUser(createdUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(userDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(
            @PathVariable long id
    ) {
        UserDto userDto = userService.getUserById(id);
        return ResponseEntity.ok(userDto);
    }

    @GetMapping("/")
    public ResponseEntity<List<UserDto>> getAllUsers(
    ) {
        List<UserDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/")
    public ResponseEntity<UserDto> updateUser(
            @RequestBody UserUpdateDto updatedUser
    ) {
        UserDto user = userService.updateUser(updatedUser);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/")
    public ResponseEntity<Void> deleteUser(
            Long id
    ) {
        userService.deleteUser(id);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
}

