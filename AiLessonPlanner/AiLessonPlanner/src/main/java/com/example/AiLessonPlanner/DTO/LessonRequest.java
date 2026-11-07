package com.example.AiLessonPlanner.DTO;

import jakarta.validation.constraints.*;
import java.util.List;
import java.util.Map;

public record LessonRequest(

        @NotNull
        @Min(1)
        @Max(12)
        Integer classNumber,

        @NotBlank
        String subject,

        @NotBlank
        String topic,

        @NotNull
        @Min(5)
        @Max(300)
        Integer duration,

        @NotBlank
        String language,

        List<String> learningObjectives,

        String introduction,

        String explanation,

        List<String> teachingActivities,

        String practicalActivity,

        List<String> assessmentQuestions,

        String homework,

        // New: all advanced lesson sections
        Map<String, Object> detailedContent

) {}