package com.example.lostandfound.controller;

import com.example.lostandfound.dto.AuthResponse;
import com.example.lostandfound.dto.SignInRequest;
import com.example.lostandfound.dto.SignUpRequest;
import com.example.lostandfound.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        try {
            String message = authService.registerUser(signUpRequest);
            return ResponseEntity.ok(message);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> authenticateUser(@Valid @RequestBody SignInRequest signInRequest) {
        AuthResponse response = authService.authenticateUser(signInRequest);
        return ResponseEntity.ok(response);
    }
}
