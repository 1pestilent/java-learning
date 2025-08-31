package com.example.demo.service;

import com.example.demo.dto.UserCreateDto;
import com.example.demo.dto.UserDto;
import com.example.demo.dto.UserUpdateDto;
import com.example.demo.exception.UserAlreadyExistsException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.model.User;
import com.example.demo.producer.UserEventProducer;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements  UserService{

    private final UserRepository userRepository;
    private final UserEventProducer userEventProducer;

    @Override
    public UserDto createUser(UserCreateDto userCreate) {
        if (userRepository.existsByUsername(userCreate.getUsername())) {
            throw new UserAlreadyExistsException(
                    "User with username '" + userCreate.getUsername() + "' already exists",
                    userCreate.getEmail(),
                    userCreate.getUsername()
            );
        }

        if (userRepository.existsByEmail(userCreate.getEmail())) {
        throw new UserAlreadyExistsException(
                "User with email '" + userCreate.getEmail() + "' already exists",
                userCreate.getEmail(),
                userCreate.getUsername()
        );
    }
    User created_user = new User();

    created_user.setUsername(userCreate.getUsername());
    created_user.setEmail(userCreate.getEmail());
    created_user.setAge(userCreate.getAge());

    UserDto new_user = UserDto.fromEntity(userRepository.save(created_user));
    userEventProducer.sendUserCreated(new_user);
    return new_user;
}

    @Override
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        return UserDto.fromEntity(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserDto> users_dto = new ArrayList<>();
        for (User user : users) {
            users_dto.add(UserDto.fromEntity(user));
        }
        return users_dto;
    }

    @Override
    public UserDto updateUser(UserUpdateDto userUpdate) {
        User existingUser = userRepository.findById(userUpdate.getId()).orElseThrow(() -> new UserNotFoundException(userUpdate.getId()));
        if (userUpdate.getUsername() != null) {
            existingUser.setUsername(userUpdate.getUsername());
        }
        if (userUpdate.getEmail() != null) {
            existingUser.setEmail(userUpdate.getEmail());
        }
        if (userUpdate.getAge() != null) {
            existingUser.setAge(Integer.parseInt(userUpdate.getAge()));
        }
        return UserDto.fromEntity(userRepository.save(existingUser));
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        userRepository.delete(user);
        userEventProducer.sendUserDeleted(UserDto.fromEntity(user));
    }
}
