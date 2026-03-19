package com.myfinance.controller;

import com.myfinance.controller.dto.AuthResponse;
import com.myfinance.controller.dto.LoginRequest;
import com.myfinance.controller.dto.RegisterRequest;
import com.myfinance.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users/current")
    public Long getCurrentUserId() {
        return userService.getCurrentUserId();
    }

    @PostMapping("/auth/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        AuthResponse response = userService.register(request.name(), request.email(), request.password());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        AuthResponse response = userService.login(request.email(), request.password());
        return ResponseEntity.ok(response);
    }
}
