package com.example.demo.service;

import com.example.demo.dto.UserCreateDto;
import com.example.demo.dto.UserDto;
import com.example.demo.exception.UserAlreadyExistsException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private Faker faker;
    private User testUser;
    private UserCreateDto testUserCreateDto;

    @BeforeEach
    void setUp() {
        faker = new Faker();
        testUser = createTestUser(1L);
        testUserCreateDto = createTestUserCreateDto();
    }

    private User createTestUser(Long id) {
        return new User(
                id,
                faker.name().firstName(),
                faker.internet().emailAddress(),
                faker.number().numberBetween(18, 65)
        );
    }

    private UserCreateDto createTestUserCreateDto() {
        return new UserCreateDto(
                faker.name().firstName(),
                faker.internet().emailAddress(),
                faker.number().numberBetween(18, 65)
        );
    }

    @Test
    void userCreateTest_returnUser() {
        UserCreateDto consistentCreateDto = createTestUserCreateDto();
        User expectedUser = new User(
                1L,
                consistentCreateDto.getUsername(),
                consistentCreateDto.getEmail(),
                consistentCreateDto.getAge()
        );

        when(userRepository.existsByEmail(consistentCreateDto.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(expectedUser);

        UserDto result = userService.createUser(consistentCreateDto);

        assertAll(
                () -> assertNotNull(result),
                () -> assertNotNull(result.getId()),
                () -> assertEquals(consistentCreateDto.getUsername(), result.getUsername()),
                () -> assertEquals(consistentCreateDto.getEmail(), result.getEmail()),
                () -> assertEquals(consistentCreateDto.getAge(), result.getAge())
        );

        verify(userRepository, times(1)).existsByEmail(consistentCreateDto.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void userCreateTest_throwUserAlreadyExistsException() {
        when(userRepository.existsByEmail(testUserCreateDto.getEmail())).thenReturn(true);

        assertAll(
                () -> assertThrows(UserAlreadyExistsException.class, () -> {
                    userService.createUser(testUserCreateDto);
                })
        );

        verify(userRepository, times(1)).existsByEmail(testUserCreateDto.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getAllUsersTest_returnUsers() {
        List<User> expectedUsers = List.of(
                createTestUser(1L),
                createTestUser(2L),
                createTestUser(3L)
        );

        when(userRepository.findAll()).thenReturn(expectedUsers);

        List<UserDto> result = userService.getAllUsers();

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(expectedUsers.size(), result.size()),
                () -> {
                    for (int i = 0; i < expectedUsers.size(); i++) {
                        User expected = expectedUsers.get(i);
                        UserDto actual = result.get(i);
                        assertAll(
                                () -> assertEquals(expected.getId(), actual.getId()),
                                () -> assertEquals(expected.getUsername(), actual.getUsername()),
                                () -> assertEquals(expected.getEmail(), actual.getEmail()),
                                () -> assertEquals(expected.getAge(), actual.getAge())
                        );
                    }
                }
        );

        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getAllUsersTest_returnEmptyList() {
        when(userRepository.findAll()).thenReturn(List.of());

        List<UserDto> result = userService.getAllUsers();

        assertAll(
                () -> assertNotNull(result),
                () -> assertTrue(result.isEmpty()),
                () -> assertEquals(0, result.size())
        );

        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getUserByIdTest() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        UserDto result = userService.getUserById(1L);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(testUser.getId(), result.getId()),
                () -> assertEquals(testUser.getUsername(), result.getUsername()),
                () -> assertEquals(testUser.getEmail(), result.getEmail()),
                () -> assertEquals(testUser.getAge(), result.getAge())
        );

        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getUserByIdTest_throwUserNotFoundException() {
        Long nonExistentId = 999L;
        when(userRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertAll(
                () -> assertThrows(UserNotFoundException.class, () -> {
                    userService.getUserById(nonExistentId);
                })
        );

        verify(userRepository, times(1)).findById(nonExistentId);
    }

    @Test
    void deleteUserTest() {
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        doNothing().when(userRepository).delete(testUser);

        userService.deleteUser(testUser.getId());


        assertAll(
                () -> verify(userRepository, times(1)).findById(testUser.getId()),
                () -> verify(userRepository, times(1)).delete(testUser)
        );
    }

    @Test
    void deleteUserTest_throwUserNotFoundException() {
        Long nonExistentId = 999L;
        when(userRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertAll(
                () -> assertThrows(UserNotFoundException.class, () -> {
                    userService.deleteUser(nonExistentId);
                }),
                () -> verify(userRepository, times(1)).findById(nonExistentId),
                () -> verify(userRepository, never()).delete(any(User.class))
        );
    }

}