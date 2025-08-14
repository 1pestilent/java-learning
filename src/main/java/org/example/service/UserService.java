package org.example.service;

import org.example.model.User;
import java.util.List;

public interface UserService {
    User createUser(String name, String email, int age);
    User getUserById(Long id);
    List<User> getAllUsers();
    User updateUser(Long id, String name, String email, int age);
    void deleteUser(Long id);
}