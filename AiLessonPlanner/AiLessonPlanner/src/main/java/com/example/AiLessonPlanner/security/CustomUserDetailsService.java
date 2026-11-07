package com.example.AiLessonPlanner.security;

import com.example.AiLessonPlanner.entity.Teacher;
import com.example.AiLessonPlanner.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService
        implements UserDetailsService {

    private final TeacherRepository teacherRepository;

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        Teacher teacher = teacherRepository
                .findByEmail(email.toLowerCase())
                .orElseThrow(() ->
                        new UsernameNotFoundException("Teacher not found")
                );

        return User.builder()
                .username(teacher.getEmail())
                .password(teacher.getPassword())
                .roles("TEACHER")
                .build();
    }
}