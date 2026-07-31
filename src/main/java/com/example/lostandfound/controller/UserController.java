package com.example.lostandfound.controller;

import com.example.lostandfound.entity.User;
import com.example.lostandfound.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for User management.
 *
 * Endpoint summary:
 *  GET    /api/users       – List all users        (ADMIN only)
 *  GET    /api/users/{id}  – Get user by ID        (ADMIN only)
 *  PUT    /api/users/{id}  – Update user           (ADMIN only)
 *  DELETE /api/users/{id}  – Delete user           (ADMIN only)
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    // -------------------------------------------------------------------------
    // GET /api/users
    // -------------------------------------------------------------------------
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getAllUsers() {
        logger.info("GET /api/users – listing all users");
        return userService.getAllUsers();
    }

    // -------------------------------------------------------------------------
    // GET /api/users/{id}
    // -------------------------------------------------------------------------
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        logger.info("GET /api/users/{} – fetching user", id);
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // -------------------------------------------------------------------------
    // PUT /api/users/{id}
    // -------------------------------------------------------------------------
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        logger.info("PUT /api/users/{} – updating user", id);
        User updated = userService.updateUser(id, userDetails);
        return ResponseEntity.ok(updated);
    }

    // -------------------------------------------------------------------------
    // DELETE /api/users/{id}
    // -------------------------------------------------------------------------
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        logger.info("DELETE /api/users/{} – deleting user", id);
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully.");
    }
}
