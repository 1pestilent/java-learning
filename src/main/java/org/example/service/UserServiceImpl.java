package org.example.service;

import org.example.dao.UserDao;
import org.example.model.User;
import java.util.List;

public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public User createUser(String name, String email, int age) {

        User user = new User(name, email, age);
        user = userDao.save(user);
        return user;
    }

    @Override
    public User getUserById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid user id");
        }

        User user = userDao.findById(id);
        if (user == null) {
            throw new RuntimeException("User not found with id: " + id);
        }
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    @Override
    public User updateUser(Long id, String name, String email, int age) {

        User user = getUserById(id);
        user.setName(name);
        user.setEmail(email);
        user.setAge(age);

        userDao.update(user);
        return user;
    }

    @Override
    public void deleteUser(Long id) {
        User user = getUserById(id);
        userDao.delete(user);
    }
}