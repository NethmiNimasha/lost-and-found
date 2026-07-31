package com.example.lostandfound.service;

import com.example.lostandfound.entity.User;
import com.example.lostandfound.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service layer for User management operations.
 * Read operations are available to ADMIN and STAFF.
 * Write/delete operations are restricted to ADMIN (enforced in the controller).
 */
@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Returns all registered users.
     */
    public List<User> getAllUsers() {
        logger.debug("Fetching all users");
        List<User> users = userRepository.findAll();
        logger.info("Retrieved {} user(s)", users.size());
        return users;
    }

    /**
     * Finds a user by their ID.
     *
     * @param id the user's primary key
     * @return an Optional containing the user, or empty if not found
     */
    public Optional<User> getUserById(Long id) {
        logger.debug("Fetching user with id={}", id);
        return userRepository.findById(id);
    }

    /**
     * Updates a user's username, role, and/or password.
     * Only non-null fields in {@code userDetails} are applied.
     *
     * @param id          the ID of the user to update
     * @param userDetails the request body with new field values
     * @return the updated and persisted User
     */
    public User updateUser(Long id, User userDetails) {
        logger.debug("Attempting to update user with id={}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Update failed – user not found with id={}", id);
                    return new RuntimeException("User not found with id: " + id);
                });

        if (userDetails.getUsername() != null && !userDetails.getUsername().isBlank()) {
            // Ensure the new username is not already taken by another user
            userRepository.findByUsername(userDetails.getUsername()).ifPresent(existing -> {
                if (!existing.getId().equals(id)) {
                    throw new RuntimeException("Username '" + userDetails.getUsername() + "' is already taken.");
                }
            });
            user.setUsername(userDetails.getUsername());
            logger.debug("Updating username for user id={}", id);
        }

        if (userDetails.getRole() != null) {
            user.setRole(userDetails.getRole());
            logger.debug("Updating role to {} for user id={}", userDetails.getRole(), id);
        }

        // Only re-hash if a new password string was provided
        if (userDetails.getPassword() != null && !userDetails.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
            logger.debug("Updating password for user id={}", id);
        }

        User saved = userRepository.save(user);
        logger.info("User updated successfully: id={}, username={}", saved.getId(), saved.getUsername());
        return saved;
    }

    /**
     * Permanently deletes a user by ID.
     *
     * @param id the ID of the user to delete
     */
    public void deleteUser(Long id) {
        logger.debug("Attempting to delete user with id={}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Delete failed – user not found with id={}", id);
                    return new RuntimeException("User not found with id: " + id);
                });

        userRepository.delete(user);
        logger.info("User deleted successfully: id={}, username={}", id, user.getUsername());
    }
}
