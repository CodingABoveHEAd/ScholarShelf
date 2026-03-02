package com.niloy.scholarshelf.repository;

import com.niloy.scholarshelf.entity.User;
import com.niloy.scholarshelf.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for User entity operations.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByRole(Role role);

    List<User> findByActive(Boolean active);

    long countByRole(Role role);
}

