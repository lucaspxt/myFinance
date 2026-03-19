package com.myfinance.controller.dto;

public record AuthResponse(String token, Long userId, String name) {}
