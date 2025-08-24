package com.example.demo.service;

import com.example.demo.dto.UserCreateDto;
import com.example.demo.dto.UserDto;
import com.example.demo.dto.UserUpdateDto;
import com.example.demo.exception.UserAlreadyExistsException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public UserDto createUser(UserCreateDto user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new UserAlreadyExistsException(
                    "User with username '" + user.getUsername() + "' already exists",
                    user.getEmail(),
                    user.getUsername()
            );
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new UserAlreadyExistsException(
                    "User with email '" + user.getEmail() + "' already exists",
                    user.getEmail(),
                    user.getUsername()
            );
        }

        User created_user = new User();

        created_user.setUsername(user.getUsername());
        created_user.setEmail(user.getEmail());
        created_user.setAge(user.getAge());

        return UserDto.fromEntity(userRepository.save(created_user));
    }

    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        return UserDto.fromEntity(user);
    }

    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserDto> users_dto = new ArrayList<>();
        for (User user : users) {
            users_dto.add(UserDto.fromEntity(user));
        }
        return users_dto;
    }

    public UserDto updateUser(UserUpdateDto updatedUser) {
        User existingUser = userRepository.findById(updatedUser.getId()).orElseThrow(() -> new UserNotFoundException(updatedUser.getId()));
        if (updatedUser.getUsername() != null) {
            existingUser.setUsername(updatedUser.getUsername());
        }
        if (updatedUser.getEmail() != null) {
            existingUser.setEmail(updatedUser.getEmail());
        }
        if (updatedUser.getAge() != null) {
            existingUser.setAge(Integer.parseInt(updatedUser.getAge()));
        }
        return UserDto.fromEntity(userRepository.save(existingUser));
    }

    public boolean delete(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        userRepository.delete(user);
        return true;
    }
}
