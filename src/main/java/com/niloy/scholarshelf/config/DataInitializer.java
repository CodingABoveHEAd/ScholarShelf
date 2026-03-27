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
        log.info("Initializing default users...");
        String defaultPassword = "password123";

        createOrActivateUser("Admin User", "admin@scholarshelf.com", defaultPassword, Role.ADMIN,
                "+1234567890", "Dhaka, Bangladesh");
        createOrActivateUser("John Seller", "seller@scholarshelf.com", defaultPassword, Role.SELLER,
                "+1234567891", "Chittagong, Bangladesh");
        createOrActivateUser("Jane Buyer", "buyer@scholarshelf.com", defaultPassword, Role.BUYER,
                "+1234567892", "Sylhet, Bangladesh");
        createOrActivateUser("Alice Seller", "alice@scholarshelf.com", defaultPassword, Role.SELLER,
                "+1234567893", "Rajshahi, Bangladesh");

        log.info("Default users ready. Password for all users: password123");
    }

    private void createCategory(String name, String description) {
        if (!categoryRepository.existsByName(name)) {
            categoryRepository.save(Category.builder().name(name).description(description).build());
        }
    }

    private void createOrActivateUser(String fullName, String email, String password, Role role,
                                      String phone, String address) {
        var existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            // User exists - reconcile key fields so seeded credentials keep working.
            User user = existingUser.get();
            boolean changed = false;

            if (!user.getActive()) {
                user.setActive(true);
                changed = true;
            }

            user.setPassword(passwordEncoder.encode(password));
            changed = true;

            if (user.getRole() != role) {
                user.setRole(role);
                changed = true;
            }

            if (!fullName.equals(user.getFullName())) {
                user.setFullName(fullName);
                changed = true;
            }

            if (!phone.equals(user.getPhone())) {
                user.setPhone(phone);
                changed = true;
            }

            if (!address.equals(user.getAddress())) {
                user.setAddress(address);
                changed = true;
            }

            if (changed) {
                userRepository.save(user);
                log.info("Synchronized existing user: {}", email);
            }
        } else {
            // Create new user
            userRepository.save(User.builder()
                    .fullName(fullName)
                    .email(email)
                    .password(passwordEncoder.encode(password))
                    .role(role)
                    .phone(phone)
                    .address(address)
                    .active(true)
                    .build());
            log.info("Created new user: {}", email);
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

