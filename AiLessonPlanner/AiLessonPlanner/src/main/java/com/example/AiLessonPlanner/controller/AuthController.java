package com.example.AiLessonPlanner.controller;

import com.example.AiLessonPlanner.DTO.AuthResponse;
import com.example.AiLessonPlanner.DTO.LoginRequest;
import com.example.AiLessonPlanner.DTO.RegisterRequest;
import com.example.AiLessonPlanner.entity.Teacher;
import com.example.AiLessonPlanner.repository.TeacherRepository;
import com.example.AiLessonPlanner.service.AuthService;
import com.example.AiLessonPlanner.service.RefreshTokenService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final TeacherRepository teacherRepository;

    @Value("${app.cookie.secure:false}")
    private boolean secureCookie;

    @Value("${app.refresh.expiration-ms}")
    private long refreshExpiration;

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Valid @RequestBody RegisterRequest request
    ) {

        Teacher teacher = authService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                        "message", "Registration successful",
                        "email", teacher.getEmail()
                ));
    }

    @PostMapping("/login")
    public AuthResponse login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response
    ) {

        AuthResponse auth =
                authService.login(request);

        Teacher teacher = teacherRepository
                .findByEmail(auth.email())
                .orElseThrow();

        String refreshToken =
                refreshTokenService.createRefreshToken(teacher);

        setRefreshCookie(response, refreshToken);

        return auth;
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(
            @CookieValue(
                    name = "refresh_token",
                    required = false
            ) String token,
            HttpServletResponse response
    ) {

        try {

            Teacher teacher =
                    refreshTokenService.rotateRefreshToken(token);

            String newRefreshToken =
                    refreshTokenService.createRefreshToken(teacher);

            setRefreshCookie(response, newRefreshToken);

            return ResponseEntity.ok(
                    authService.generateAccessToken(teacher)
            );

        } catch (RuntimeException e) {

            clearCookie(response);

            return ResponseEntity.status(401)
                    .body(Map.of("message", "Invalid refresh token"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @CookieValue(
                    name = "refresh_token",
                    required = false
            ) String token,
            HttpServletResponse response
    ) {

        refreshTokenService.revokeRefreshToken(token);

        clearCookie(response);

        return ResponseEntity.ok(
                Map.of("message", "Logout successful")
        );
    }

    private void setRefreshCookie(
            HttpServletResponse response,
            String token
    ) {

        ResponseCookie cookie = ResponseCookie
                .from("refresh_token", token)
                .httpOnly(true)
                .secure(secureCookie)
                .sameSite("Lax")
                .path("/api/auth")
                .maxAge(Duration.ofMillis(refreshExpiration))
                .build();

        response.addHeader(
                HttpHeaders.SET_COOKIE,
                cookie.toString()
        );
    }

    private void clearCookie(HttpServletResponse response) {

        ResponseCookie cookie = ResponseCookie
                .from("refresh_token", "")
                .httpOnly(true)
                .secure(secureCookie)
                .sameSite("Lax")
                .path("/api/auth")
                .maxAge(Duration.ZERO)
                .build();

        response.addHeader(
                HttpHeaders.SET_COOKIE,
                cookie.toString()
        );
    }
}