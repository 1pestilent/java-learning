package org.example.service;

import org.example.dao.UserDao;
import org.example.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceUnitTest {

    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void createUserTest() {
        User expectedUser = new User("Тестировщик Иван", "Иван@тестировщик.ру", 30);
        expectedUser.setId(1L);

        when(userDao.save(any(User.class))).thenReturn(expectedUser);

        User createdUser = userService.createUser("Тестировщик Иван", "Иван@тестировщик.ру", 30);
        assertNotNull(createdUser.getId());
        assertEquals("Тестировщик Иван", createdUser.getName());
        verify(userDao).save(any(User.class));
    }

    @Test
    void getUserByIdTest_returnUser() {
        User expectedUser = new User("Тестировщик Иван", "Иван@тестировщик.ру", 30);
        expectedUser.setId(1L);

        when(userDao.findById(1L)).thenReturn(expectedUser);

        User user = userService.getUserById(1L);

        assertEquals(expectedUser, user);
    }

    @Test
    void getAllUsersTest_returnSize() {
        List<User> expectedUsers = List.of(
                new User("Тестировщик Иван", "Иван@тестировщик.ру", 30),
                new User("Тестировщик Коля", "Коля@тестировщик.ру", 22)
        );

        when(userDao.findAll()).thenReturn(expectedUsers);

        List<User> users = userService.getAllUsers();

        assertEquals(2, users.size());
    }

    @Test
    void getAllUsersTest_returnUsers() {
        List<User> expectedUsers = List.of(
                new User("Тестировщик Иван", "Иван@тестировщик.ру", 30),
                new User("Тестировщик Коля", "Коля@тестировщик.ру", 22)
        );

        when(userDao.findAll()).thenReturn(expectedUsers);

        List<User> users = userService.getAllUsers();
        assertEquals(expectedUsers, users);
    }

    @Test
    void updateUserTest() {
        User existingUser = new User("Тестировщик Иван", "Иван@тестировщик.ру", 30);
        existingUser.setId(1L);

        when(userDao.findById(1L)).thenReturn(existingUser);

        User updatedUser = userService.updateUser(1L, "Коля", "Коля@тестировщик.ру", 22);

        assertEquals("Коля", updatedUser.getName());
        assertEquals("Коля@тестировщик.ру", updatedUser.getEmail());
        assertEquals(22, updatedUser.getAge());
        verify(userDao).update(existingUser);
    }
}