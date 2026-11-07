package com.example.AiLessonPlanner.entity;


import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;

    @Column(nullable = false, unique = true)
    private String tokenHash;

    private LocalDateTime expiryDate;

    private boolean revoked;

    public Long getId() {
        return id;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public void setTokenHash(String tokenHash) {
        this.tokenHash = tokenHash;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public void setRevoked(boolean revoked) {
        this.revoked = revoked;
    }
}