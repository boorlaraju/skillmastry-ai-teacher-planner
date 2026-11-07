package com.example.AiLessonPlanner.service;

import com.example.AiLessonPlanner.DTO.AuthResponse;
import com.example.AiLessonPlanner.DTO.LoginRequest;
import com.example.AiLessonPlanner.DTO.RegisterRequest;
import com.example.AiLessonPlanner.entity.Teacher;
import com.example.AiLessonPlanner.repository.TeacherRepository;
import com.example.AiLessonPlanner.security.JwtService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final TeacherRepository teacherRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public Teacher register(RegisterRequest request) {

        String email = request.email().trim().toLowerCase();

        if (teacherRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already registered");
        }

        Teacher teacher = new Teacher();

        teacher.setEmail(email);

        teacher.setPassword(
                passwordEncoder.encode(request.password())
        );

        return teacherRepository.save(teacher);
    }

    public AuthResponse login(LoginRequest request) {

        String email = request.email().trim().toLowerCase();

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                email,
                                request.password()
                        )
                );

        String token = jwtService.generateToken(email);

        return new AuthResponse(
                token,
                "Bearer",
                email
        );
    }

    public AuthResponse generateAccessToken(Teacher teacher) {

        String token =
                jwtService.generateToken(teacher.getEmail());

        return new AuthResponse(
                token,
                "Bearer",
                teacher.getEmail()
        );
    }
}
