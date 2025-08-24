package com.example.demo.service;

import com.example.demo.dto.UserCreateDto;
import com.example.demo.dto.UserDto;
import com.example.demo.exception.UserAlreadyExistsException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void userCreateTest_returnUser() {
        UserCreateDto createDto = new UserCreateDto("Ivan", "Ivan@Ivan.ru", 11);

        User new_user = new User(1L, "Ivan","Ivan@Ivan.ru", 11);
        when(userRepository.save(any(User.class))).thenReturn(new_user);

        UserDto result = userService.createUser(createDto);

        assertNotNull(result.getId());
        assertEquals(createDto.getUsername(), result.getUsername());
        assertEquals(createDto.getEmail(), result.getEmail());
        assertEquals(createDto.getAge(), result.getAge());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void userCreateTest_throwUserAlreadyExistsException() {
        UserCreateDto createDto = new UserCreateDto("Ivan", "Ivan@Ivan.ru", 11);

        when(userRepository.existsByEmail("Ivan@Ivan.ru")).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> {
            userService.createUser(createDto);
        });
        verify(userRepository, never()).save(any(User.class));
        verify(userRepository, times(1)).existsByEmail("Ivan@Ivan.ru");
    }

    @Test
    void getAllUsersTest_returnUsers() {
        List<User> expectedUsers = List.of(
                new User(1L, "Ivan", "Ivan@Ivan.ru", 11),
                new User(2L, "Maxim", "Maxim@Ivan.ru", 33)
        );

        when(userRepository.findAll()).thenReturn(expectedUsers);

        List<UserDto> users = userService.getAllUsers();
        List<UserDto> expectedUsersDto = new ArrayList<>();
        for (User expectedUser : expectedUsers) {
            expectedUsersDto.add(UserDto.fromEntity(expectedUser));
        }

        assertEquals(expectedUsersDto, users);
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getAllUsersTest_returnEmptyList() {
        when(userRepository.findAll()).thenReturn(List.of());

        List<UserDto> result = userService.getAllUsers();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getUserByIdTest() {
        User expectedUser = new User(1L, "Ivan", "Ivan@Ivan.ru", 11);

        when(userRepository.findById(1L)).thenReturn(Optional.of(expectedUser));

        UserDto result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(expectedUser.getId(), result.getId());
        assertEquals(expectedUser.getUsername(), result.getUsername());
        assertEquals(expectedUser.getEmail(), result.getEmail());
        assertEquals(expectedUser.getAge(), result.getAge());

        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getUserByIdTest_throwUserNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> {
            userService.getUserById(1L);
        });
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void deleteUserTest() {
        User user = new User(1L, "Ivan", "Ivan@Ivan.ru", 11);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        doNothing().when(userRepository).delete(user);

        userService.delete(user.getId());

        verify(userRepository, times(1)).findById(user.getId());
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void deleteUserTest_throwUserNotFoundException() {
        Long id = 999L;

        assertThrows(UserNotFoundException.class, () -> {
            userService.delete(id);
        });

        verify(userRepository, times(1)).findById(id);
        verify(userRepository, never()).delete(any());
    }
}
