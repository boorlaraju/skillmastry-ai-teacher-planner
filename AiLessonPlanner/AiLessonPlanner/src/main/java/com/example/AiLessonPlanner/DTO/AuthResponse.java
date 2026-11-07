package com.example.AiLessonPlanner.DTO;

public record AuthResponse(
        String accessToken,
        String tokenType,
        String email
) {}
