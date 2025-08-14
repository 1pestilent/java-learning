package org.example.dao;

import org.example.model.User;
import org.example.util.HibernateUtil;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class UserDaoIntegrationTest {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    private static UserDao userDao;

    @BeforeAll
    static void setup() {
        System.setProperty("hibernate.connection.url", postgres.getJdbcUrl());
        System.setProperty("hibernate.connection.username", postgres.getUsername());
        System.setProperty("hibernate.connection.password", postgres.getPassword());

        userDao = new UserDaoImpl();
    }

    @AfterEach
    void cleanup() {
        try (var session = HibernateUtil.getSessionFactory().openSession()) {
            var transaction = session.beginTransaction();
            session.createQuery("delete from User").executeUpdate();
            transaction.commit();
        }
    }

    @Test
    void findByIdTest() {
        User user = new User("Тестировщик Иван", "Иван@тестировщик.ру", 30);
        userDao.save(user);

        User found = userDao.findById(user.getId());

        assertNotNull(found);
        assertEquals(user.getName(), found.getName());
        assertEquals(user.getEmail(), found.getEmail());
    }

    @Test
    void updateTest() {
        User user = new User("Тестировщик Иван", "Иван@тестировщик.ру", 30);
        userDao.save(user);

        user.setName("Updated Name");
        userDao.update(user);

        User updated = userDao.findById(user.getId());
        assertEquals("Updated Name", updated.getName());
    }
    @Test
    void findAllTest() {
        User user1 = new User("Тестировщик Иван", "Иван@тестировщик.ру", 30);
        User user2 = new User("Тестировщик Коля", "Коля@тестировщик.ру", 22);

        userDao.save(user1);
        userDao.save(user2);

        User found1 = userDao.findById(user1.getId());
        User found2 = userDao.findById(user2.getId());

        List<User> users = userDao.findAll();
        assertEquals(2, users.size());

        assertEquals(found1.getName(), users.get(0).getName());
        assertEquals(found2.getName(), users.get(1).getName());
    }
}
