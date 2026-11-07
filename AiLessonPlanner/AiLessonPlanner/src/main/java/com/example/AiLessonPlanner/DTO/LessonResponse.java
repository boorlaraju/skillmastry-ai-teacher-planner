package com.example.AiLessonPlanner.DTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record LessonResponse(

        Long id,
        Integer classNumber,
        String subject,
        String topic,
        Integer duration,
        String language,

        List<String> learningObjectives,
        String introduction,
        String explanation,
        List<String> teachingActivities,
        String practicalActivity,
        List<String> assessmentQuestions,
        String homework,

        // New detailed lesson content
        Map<String, Object> detailedContent,

        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {}