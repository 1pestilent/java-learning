package org.example.dao;

import org.example.model.User;
import org.example.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import java.util.List;

public class UserDaoImpl implements UserDao {
    @Override
    public User save(User user) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(user);
            transaction.commit();
            System.out.println(String.format("Пользователь успешно создан. ID: %d, Имя: %s", user.getId(), user.getName()));

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("Ошибка при создании пользователя: " + e.getMessage());
            throw e;
        }
        return user;
    }

    @Override
    public User findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            User user = session.get(User.class, id);
            if (user == null) {
                System.out.println(String.format("Пользователь с ID %d не найден", id));
                return null;
            }
            System.out.println(String.format("\nДанные пользователя:" +
                            "\nID: %d" +
                            "\nИмя: %s" +
                            "\nEmail: %s" +
                            "\nВозраст: %d" +
                            "\nДата регистрации: %s\n",
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getAge(),
                    user.getCreatedAt()));
            return user;
        }
    }

    @Override
    public List<User> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<User> query = session.createQuery("from User", User.class);
            List<User> users = query.list();

            System.out.println("\nСписок пользователей:");
            users.forEach(user -> System.out.println(
                    String.format("ID: %d | Имя: %-15s | Email: %-20s | Возраст: %d",
                            user.getId(),
                            user.getName(),
                            user.getEmail(),
                            user.getAge())
            ));
                System.out.println("Всего пользователей:" + users.size());

            return users;
        }
    }

    @Override
    public void update(User user) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(user);
            transaction.commit();
            System.out.println(String.format("Данные пользователя обновлены. ID: %d", user.getId()));
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("Ошибка при обновлении пользователя: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void delete(User user) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.delete(user);
            transaction.commit();
            System.out.println(String.format("Пользователь удалён. ID: %d", user.getId()));
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("Ошибка при удалении пользователя: " + e.getMessage());
            throw e;
        }
    }
}