package com.myfinance.service;

import com.myfinance.config.JwtService;
import com.myfinance.controller.dto.AuthResponse;
import com.myfinance.model.User;
import com.myfinance.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class UserServiceTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtService jwtService;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        passwordEncoder = new BCryptPasswordEncoder();
        jwtService = Mockito.mock(JwtService.class);
        userService = new UserService(userRepository, passwordEncoder, jwtService);
        SecurityContextHolder.clearContext();
    }

    @Test
    void getCurrentUserId_returnsIdFromSecurityContext() {
        Long expectedId = 42L;
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(expectedId, null, List.of()));

        Long userId = userService.getCurrentUserId();

        assertNotNull(userId);
        assertEquals(expectedId, userId);
    }

    @Test
    void getCurrentUserId_throwsWhenNoAuthentication() {
        assertThrows(IllegalStateException.class, () -> userService.getCurrentUserId());
    }

    @Test
    void register_savesUserAndReturnsToken() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        User savedUser = new User("Test", "test@example.com", "hashed");
        savedUser.setId(1L);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtService.generateToken(1L)).thenReturn("jwt-token");

        AuthResponse response = userService.register("Test", "test@example.com", "password");

        assertNotNull(response);
        assertEquals("jwt-token", response.token());
        assertEquals(1L, response.userId());
        assertEquals("Test", response.name());
    }

    @Test
    void register_throwsWhenEmailAlreadyExists() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> userService.register("Test", "test@example.com", "password"));
    }

    @Test
    void login_returnsTokenForValidCredentials() {
        String rawPassword = "password";
        User user = new User("Test", "test@example.com", passwordEncoder.encode(rawPassword));
        user.setId(1L);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(1L)).thenReturn("jwt-token");

        AuthResponse response = userService.login("test@example.com", rawPassword);

        assertNotNull(response);
        assertEquals("jwt-token", response.token());
        assertEquals(1L, response.userId());
    }

    @Test
    void login_throwsForInvalidPassword() {
        User user = new User("Test", "test@example.com", passwordEncoder.encode("correct-pass"));
        user.setId(1L);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class,
                () -> userService.login("test@example.com", "wrong-pass"));
    }
}
