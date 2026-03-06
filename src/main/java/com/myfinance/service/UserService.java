package com.myfinance.service;

import org.springframework.stereotype.Service;

@Service
public class UserService {

    private static final Long HARDCODED_USER_ID = 1L;

    public Long getCurrentUserId() {
        return HARDCODED_USER_ID;
    }
}
