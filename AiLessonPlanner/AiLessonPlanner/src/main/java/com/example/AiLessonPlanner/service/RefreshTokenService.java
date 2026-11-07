package com.example.AiLessonPlanner.service;

import com.example.AiLessonPlanner.entity.RefreshToken;
import com.example.AiLessonPlanner.entity.Teacher;
import com.example.AiLessonPlanner.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository repository;

    @Value("${app.refresh.expiration-ms}")
    private long expiration;

    private String hashToken(String token) {

        try {

            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                    token.getBytes(StandardCharsets.UTF_8)
            );

            return HexFormat.of().formatHex(hash);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String createRefreshToken(Teacher teacher) {

        byte[] random = new byte[64];

        new SecureRandom().nextBytes(random);

        String token = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(random);

        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setTeacher(teacher);

        refreshToken.setTokenHash(hashToken(token));

        refreshToken.setExpiryDate(
                LocalDateTime.now().plusNanos(
                        expiration * 1_000_000
                )
        );

        refreshToken.setRevoked(false);

        repository.save(refreshToken);

        return token;
    }

    @Transactional
    public Teacher rotateRefreshToken(String token) {

        if (token == null || token.isBlank()) {
            throw new RuntimeException("Refresh token missing");
        }

        RefreshToken refreshToken = repository
                .findByTokenHash(hashToken(token))
                .orElseThrow(() ->
                        new RuntimeException("Invalid refresh token")
                );

        if (refreshToken.isRevoked() ||
                refreshToken.getExpiryDate().isBefore(
                        LocalDateTime.now()
                )) {

            throw new RuntimeException("Refresh token expired");
        }

        refreshToken.setRevoked(true);

        repository.save(refreshToken);

        return refreshToken.getTeacher();
    }

    @Transactional
    public void revokeRefreshToken(String token) {

        if (token == null || token.isBlank()) {
            return;
        }

        repository.findByTokenHash(hashToken(token))
                .ifPresent(refreshToken -> {
                    refreshToken.setRevoked(true);
                    repository.save(refreshToken);
                });
    }
}