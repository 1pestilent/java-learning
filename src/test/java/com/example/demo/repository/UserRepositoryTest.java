package com.example.demo.repository;


import com.example.demo.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@SpringBootTest
public class UserRepositoryTest {
    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private UserRepository userRepository;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @AfterEach
    void cleanup() {
        userRepository.deleteAll();
    }

    @Test
    void saveUserTest() {

        User user = new User();
        user.setUsername("Ivan");
        user.setEmail("Ivan@Ivan.ru");
        user.setAge(11);

        User savedUser = userRepository.save(user);
        Optional<User> foundUser = userRepository.findById(savedUser.getId());

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("Ivan@Ivan.ru");
    }
    @Test
    void findAllUsersTest() {
        // Given
        User user1 = new User();
        user1.setUsername("Ivan");
        user1.setEmail("Ivan@Ivan.ru");
        user1.setAge(11);

        User user2 = new User();
        user2.setUsername("Maxim");
        user2.setEmail("Maxim@Ivan.ru");
        user2.setAge(33);

        userRepository.saveAll(List.of(user1, user2));
        userRepository.flush();

        List<User> users = userRepository.findAll();

        assertEquals(2, users.size());
    }

    @Test
    void updateUserTest() {
        User user = new User();
        user.setUsername("Ivan");
        user.setEmail("Ivan@Ivan.ru");
        user.setAge(11);

        User savedUser = userRepository.save(user);
        userRepository.flush();

        savedUser.setUsername("NotIvan");
        savedUser.setAge(26);

        User updatedUser = userRepository.save(savedUser);
        userRepository.flush();

        Optional<User> foundUser = userRepository.findById(updatedUser.getId());
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("NotIvan");
        assertThat(foundUser.get().getAge()).isEqualTo(26);
    }

    @Test
    void deleteUserTest() {
        User user = new User();
        user.setUsername("Ivan");
        user.setEmail("Ivan@Ivan.ru");
        user.setAge(11);


        User savedUser = userRepository.save(user);
        userRepository.flush();

        userRepository.deleteById(savedUser.getId());
        userRepository.flush();

        Optional<User> foundUser = userRepository.findById(savedUser.getId());
        assertThat(foundUser).isEmpty();
    }

    @Test
    void existUserByEmail() {
        User user = new User();
        user.setUsername("Ivan");
        user.setEmail("Ivan@Ivan.ru");
        user.setAge(11);

        userRepository.save(user);
        userRepository.flush();

        assertThat(userRepository.existsByEmail("Ivan@Ivan.ru")).isTrue();
        assertThat(userRepository.existsByEmail("notIvan@Ivan.ru")).isFalse();
    }

    @Test
    void existUserByUsername() {
        User user = new User();
        user.setUsername("Ivan");
        user.setEmail("Ivan@Ivan.ru");
        user.setAge(11);

        userRepository.save(user);
        userRepository.flush();

        assertThat(userRepository.existsByUsername("Ivan")).isTrue();
        assertThat(userRepository.existsByEmail("notIvan")).isFalse();
    }
}
