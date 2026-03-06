package com.myfinance.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UserServiceTest {

    private final UserService userService = new UserService();

    @Test
    void getCurrentUserId_returnsHardcodedId() {
        Long userId = userService.getCurrentUserId();
        assertNotNull(userId);
        assertEquals(1L, userId);
    }
}
