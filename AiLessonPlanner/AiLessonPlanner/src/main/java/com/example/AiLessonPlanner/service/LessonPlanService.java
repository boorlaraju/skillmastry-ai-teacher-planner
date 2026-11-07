package com.example.AiLessonPlanner.service;

import com.example.AiLessonPlanner.DTO.LessonRequest;
import com.example.AiLessonPlanner.DTO.LessonResponse;
import com.example.AiLessonPlanner.entity.LessonPlan;
import com.example.AiLessonPlanner.entity.Teacher;
import com.example.AiLessonPlanner.repository.LessonPlanRepository;
import com.example.AiLessonPlanner.repository.TeacherRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.*;

@Service
@RequiredArgsConstructor
public class LessonPlanService {

    private final LessonPlanRepository lessonRepository;
    private final TeacherRepository teacherRepository;
    private final AiLessonService aiLessonService;

    // Jackson 3, used by Spring Boot 4
    private final ObjectMapper mapper = new ObjectMapper();

    private Teacher getTeacher(String email) {

        return teacherRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                "Teacher not found"
                        )
                );
    }

    // Generate a lesson without saving it
    public LessonResponse generate(LessonRequest request) {

        LessonRequest generated =
                aiLessonService.generateLesson(request);

        return convertGenerated(generated);
    }

    // Save a generated lesson
    public LessonResponse create(
            String email,
            LessonRequest request
    ) {

        Teacher teacher = getTeacher(email);

        LessonPlan lesson = new LessonPlan();

        lesson.setTeacher(teacher);

        updateEntity(lesson, request);

        return convert(
                lessonRepository.save(lesson)
        );
    }

    // Get all lessons belonging to the logged-in teacher
    public List<LessonResponse> getAll(String email) {

        Teacher teacher = getTeacher(email);

        return lessonRepository
                .findByTeacherIdOrderByUpdatedAtDesc(teacher.getId())
                .stream()
                .map(this::convert)
                .toList();
    }

    // Get one lesson
    public LessonResponse getById(String email, Long id) {

        return convert(findOwnedLesson(email, id));
    }

    // Update a lesson
    public LessonResponse update(
            String email,
            Long id,
            LessonRequest request
    ) {

        LessonPlan lesson = findOwnedLesson(email, id);

        updateEntity(lesson, request);

        return convert(
                lessonRepository.save(lesson)
        );
    }

    // Delete a lesson
    public void delete(String email, Long id) {

        LessonPlan lesson = findOwnedLesson(email, id);

        lessonRepository.delete(lesson);
    }

    public long count(String email) {

        return lessonRepository.countByTeacherId(
                getTeacher(email).getId()
        );
    }

    // Ensure teachers can access only their own lessons
    private LessonPlan findOwnedLesson(
            String email,
            Long id
    ) {

        Teacher teacher = getTeacher(email);

        return lessonRepository
                .findByIdAndTeacherId(id, teacher.getId())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Lesson plan not found"
                        )
                );
    }

    // Copy request data into the entity
    private void updateEntity(
            LessonPlan lesson,
            LessonRequest request
    ) {

        lesson.setClassNumber(request.classNumber());
        lesson.setSubject(request.subject());
        lesson.setTopic(request.topic());
        lesson.setDuration(request.duration());
        lesson.setLanguage(request.language());

        lesson.setLearningObjectives(
                writeJson(request.learningObjectives())
        );

        lesson.setIntroduction(request.introduction());
        lesson.setExplanation(request.explanation());

        lesson.setTeachingActivities(
                writeJson(request.teachingActivities())
        );

        lesson.setPracticalActivity(
                request.practicalActivity()
        );

        lesson.setAssessmentQuestions(
                writeJson(request.assessmentQuestions())
        );

        lesson.setHomework(request.homework());

        lesson.setDetailedContent(
                writeJson(request.detailedContent())
        );
    }

    // Convert Java objects to JSON strings
    private String writeJson(Object value) {

        try {
            return mapper.writeValueAsString(
                    value == null ? Map.of() : value
            );
        } catch (Exception e) {
            throw new RuntimeException(
                    "Unable to convert lesson content to JSON", e
            );
        }
    }

    // Convert JSON strings back to lists
    private List<String> readList(String json) {

        try {
            return json == null
                    ? List.of()
                    : mapper.readValue(
                    json,
                    new TypeReference<List<String>>() {}
            );
        } catch (Exception e) {
            return List.of();
        }
    }

    // Convert JSON string back to detailed content
    private Map<String, Object> readDetailedContent(String json) {

        try {
            return json == null
                    ? Map.of()
                    : mapper.readValue(
                    json,
                    new TypeReference<Map<String, Object>>() {}
            );
        } catch (Exception e) {
            return Map.of();
        }
    }

    // Convert database entity to API response
    private LessonResponse convert(LessonPlan lesson) {

        return new LessonResponse(
                lesson.getId(),
                lesson.getClassNumber(),
                lesson.getSubject(),
                lesson.getTopic(),
                lesson.getDuration(),
                lesson.getLanguage(),

                readList(lesson.getLearningObjectives()),
                lesson.getIntroduction(),
                lesson.getExplanation(),
                readList(lesson.getTeachingActivities()),
                lesson.getPracticalActivity(),
                readList(lesson.getAssessmentQuestions()),
                lesson.getHomework(),

                readDetailedContent(lesson.getDetailedContent()),

                lesson.getCreatedAt(),
                lesson.getUpdatedAt()
        );
    }

    // Convert generated lesson to API response
    private LessonResponse convertGenerated(
            LessonRequest request
    ) {

        return new LessonResponse(
                null,
                request.classNumber(),
                request.subject(),
                request.topic(),
                request.duration(),
                request.language(),

                request.learningObjectives(),
                request.introduction(),
                request.explanation(),
                request.teachingActivities(),
                request.practicalActivity(),
                request.assessmentQuestions(),
                request.homework(),

                request.detailedContent(),

                null,
                null
        );
    }
}