package com.example.AiLessonPlanner.controller;

import com.example.AiLessonPlanner.DTO.LessonRequest;
import com.example.AiLessonPlanner.DTO.LessonResponse;
import com.example.AiLessonPlanner.service.LessonPlanService;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lessons")
@RequiredArgsConstructor
public class LessonPlanController {

    private final LessonPlanService lessonService;

    @PostMapping("/generate")
    public LessonResponse generate(
            @Valid @RequestBody LessonRequest request
    ) {

        return lessonService.generate(request);
    }

    @PostMapping
    public ResponseEntity<LessonResponse> create(
            Authentication authentication,
            @Valid @RequestBody LessonRequest request
    ) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        lessonService.create(
                                authentication.getName(),
                                request
                        )
                );
    }

    @GetMapping
    public List<LessonResponse> getAll(
            Authentication authentication
    ) {

        return lessonService.getAll(authentication.getName());
    }

    @GetMapping("/{id}")
    public LessonResponse getById(
            Authentication authentication,
            @PathVariable Long id
    ) {

        return lessonService.getById(
                authentication.getName(), id
        );
    }

    @PutMapping("/{id}")
    public LessonResponse update(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody LessonRequest request
    ) {

        return lessonService.update(
                authentication.getName(),
                id,
                request
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            Authentication authentication,
            @PathVariable Long id
    ) {

        lessonService.delete(
                authentication.getName(), id
        );

        return ResponseEntity.noContent().build();
    }
}