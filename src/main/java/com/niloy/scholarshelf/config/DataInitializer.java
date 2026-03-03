package com.niloy.scholarshelf.config;

import com.niloy.scholarshelf.entity.Category;
import com.niloy.scholarshelf.entity.User;
import com.niloy.scholarshelf.enums.Role;
import com.niloy.scholarshelf.repository.CategoryRepository;
import com.niloy.scholarshelf.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Initializes sample data on application startup if the database is empty.
 * This ensures proper BCrypt password encoding and data consistency.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        initializeCategories();
        initializeUsers();
    }

    private void initializeCategories() {
        if (categoryRepository.count() == 0) {
            log.info("Initializing default categories...");
            createCategory("Fiction", "Novels, short stories, and literary fiction");
            createCategory("Science", "Physics, Chemistry, Biology, and general science");
            createCategory("Technology", "Computer science, programming, and IT");
            createCategory("History", "World history, civilizations, and historical events");
            createCategory("Mathematics", "Algebra, Calculus, Statistics, and applied math");
            createCategory("Philosophy", "Ethics, logic, metaphysics, and philosophical thought");
            log.info("Default categories created successfully.");
        }
    }

    private void initializeUsers() {
        if (userRepository.count() == 0) {
            log.info("Initializing default users...");
            String encodedPassword = passwordEncoder.encode("password123");

            createUser("Admin User", "admin@scholarshelf.com", encodedPassword, Role.ADMIN,
                    "+1234567890", "Dhaka, Bangladesh");
            createUser("John Seller", "seller@scholarshelf.com", encodedPassword, Role.SELLER,
                    "+1234567891", "Chittagong, Bangladesh");
            createUser("Jane Buyer", "buyer@scholarshelf.com", encodedPassword, Role.BUYER,
                    "+1234567892", "Sylhet, Bangladesh");
            createUser("Alice Seller", "alice@scholarshelf.com", encodedPassword, Role.SELLER,
                    "+1234567893", "Rajshahi, Bangladesh");

            log.info("Default users created successfully. Password for all users: password123");
        }
    }

    private void createCategory(String name, String description) {
        if (!categoryRepository.existsByName(name)) {
            categoryRepository.save(Category.builder().name(name).description(description).build());
        }
    }

    private void createUser(String fullName, String email, String password, Role role,
                            String phone, String address) {
        if (!userRepository.existsByEmail(email)) {
            userRepository.save(User.builder()
                    .fullName(fullName)
                    .email(email)
                    .password(password)
                    .role(role)
                    .phone(phone)
                    .address(address)
                    .active(true)
                    .build());
        }
    }
}

