package com.example.AiLessonPlanner.repository;


import com.example.AiLessonPlanner.entity.LessonPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface LessonPlanRepository
        extends JpaRepository<LessonPlan, Long> {

    List<LessonPlan> findByTeacherIdOrderByUpdatedAtDesc(Long teacherId);

    Optional<LessonPlan> findByIdAndTeacherId(
            Long id, Long teacherId
    );

    long countByTeacherId(Long teacherId);
}
