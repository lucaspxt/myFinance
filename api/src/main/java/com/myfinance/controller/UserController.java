package com.myfinance.controller;

import com.myfinance.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/current")
    public ResponseEntity<Long> getCurrentUserId() {
        return ResponseEntity.ok(userService.getCurrentUserId());
    }
}
