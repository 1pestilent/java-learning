package org.example;

import org.example.dao.UserDao;
import org.example.dao.UserDaoImpl;
import org.example.model.User;
import org.example.util.HibernateUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);
    private static final UserDao userDao = new UserDaoImpl();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            logger.info("Application started");
            showMenu();
            HibernateUtil.shutdown();
        } catch (Exception e) {
            logger.error("An error occurred: ", e);
        } finally {
            scanner.close();
            logger.info("Application stopped");
        }
    }

    private static void showMenu() {
        boolean running = true;
        while (running) {
            System.out.println("\nUser Management System");
            System.out.println("1. Create User");
            System.out.println("2. View All Users");
            System.out.println("3. View User by ID");
            System.out.println("4. Update User");
            System.out.println("5. Delete User");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());
                switch (choice) {
                    case 1:
                        createUser();
                        break;
                    case 2:
                        viewAllUsers();
                        break;
                    case 3:
                        viewUserById();
                        break;
                    case 4:
                        updateUser();
                        break;
                    case 5:
                        deleteUser();
                        break;
                    case 6:
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
                logger.error("Error in menu: ", e);
            }
        }
    }

    private static void createUser() {
        System.out.println("\nCreate New User");
        System.out.print("Enter name: ");
        String name = scanner.nextLine();

        System.out.print("Enter email: ");
        String email = scanner.nextLine();

        System.out.print("Enter age: ");
        int age = Integer.parseInt(scanner.nextLine());

        User user = new User(name, email, age);
        userDao.save(user);
        System.out.println("User created successfully with ID: " + user.getId());
    }

    private static void viewAllUsers() {
        System.out.println("\nAll Users:");
        List<User> users = userDao.findAll();
        if (users.isEmpty()) {
            System.out.println("No users found.");
        } else {
            users.forEach(System.out::println);
        }
    }

    private static void viewUserById() {
        System.out.print("\nEnter user ID: ");
        Long id = Long.parseLong(scanner.nextLine());
        User user = userDao.findById(id);
        if (user != null) {
            System.out.println(user);
        } else {
            System.out.println("User not found with ID: " + id);
        }
    }

    private static void updateUser() {
        System.out.print("\nEnter user ID to update: ");
        Long id = Long.parseLong(scanner.nextLine());
        User user = userDao.findById(id);
        if (user == null) {
            System.out.println("User not found with ID: " + id);
            return;
        }

        System.out.println("Current user details: " + user);
        System.out.print("Enter new name (leave blank to keep current): ");
        String name = scanner.nextLine();
        if (!name.isEmpty()) {
            user.setName(name);
        }

        System.out.print("Enter new email (leave blank to keep current): ");
        String email = scanner.nextLine();
        if (!email.isEmpty()) {
            user.setEmail(email);
        }

        System.out.print("Enter new age (leave blank to keep current): ");
        String ageInput = scanner.nextLine();
        if (!ageInput.isEmpty()) {
            user.setAge(Integer.parseInt(ageInput));
        }

        userDao.update(user);
        System.out.println("User updated successfully.");
    }

    private static void deleteUser() {
        System.out.print("\nEnter user ID to delete: ");
        Long id = Long.parseLong(scanner.nextLine());
        User user = userDao.findById(id);
        if (user == null) {
            System.out.println("User not found with ID: " + id);
            return;
        }

        userDao.delete(user);
        System.out.println("User deleted successfully.");
    }
}