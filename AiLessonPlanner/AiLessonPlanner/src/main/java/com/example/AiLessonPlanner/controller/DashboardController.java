package com.example.AiLessonPlanner.controller;

import com.example.AiLessonPlanner.service.LessonPlanService;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final LessonPlanService lessonService;

    @GetMapping
    public Map<String, Long> dashboard(
            Authentication authentication
    ) {

        long total = lessonService.count(
                authentication.getName()
        );

        return Map.of("totalLessons", total);
    }
}