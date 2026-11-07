package com.example.AiLessonPlanner.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "lesson_plans")
public class LessonPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;

    private Integer classNumber;

    private String subject;

    private String topic;

    private Integer duration;

    private String language;

    @Column(columnDefinition = "LONGTEXT")
    private String learningObjectives;

    @Column(columnDefinition = "LONGTEXT")
    private String introduction;

    @Column(columnDefinition = "LONGTEXT")
    private String explanation;

    @Column(columnDefinition = "LONGTEXT")
    private String teachingActivities;

    @Column(columnDefinition = "LONGTEXT")
    private String practicalActivity;

    @Column(columnDefinition = "LONGTEXT")
    private String assessmentQuestions;

    @Column(columnDefinition = "LONGTEXT")
    private String homework;

    // Stores advanced lesson sections as JSON
    @Column(name = "detailed_content", columnDefinition = "LONGTEXT")
    private String detailedContent;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters

    public Long getId() { return id; }

    public Teacher getTeacher() { return teacher; }

    public Integer getClassNumber() { return classNumber; }

    public String getSubject() { return subject; }

    public String getTopic() { return topic; }

    public Integer getDuration() { return duration; }

    public String getLanguage() { return language; }

    public String getLearningObjectives() { return learningObjectives; }

    public String getIntroduction() { return introduction; }

    public String getExplanation() { return explanation; }

    public String getTeachingActivities() { return teachingActivities; }

    public String getPracticalActivity() { return practicalActivity; }

    public String getAssessmentQuestions() { return assessmentQuestions; }

    public String getHomework() { return homework; }

    public String getDetailedContent() { return detailedContent; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // Setters

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public void setClassNumber(Integer classNumber) {
        this.classNumber = classNumber;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setLearningObjectives(String learningObjectives) {
        this.learningObjectives = learningObjectives;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public void setTeachingActivities(String teachingActivities) {
        this.teachingActivities = teachingActivities;
    }

    public void setPracticalActivity(String practicalActivity) {
        this.practicalActivity = practicalActivity;
    }

    public void setAssessmentQuestions(String assessmentQuestions) {
        this.assessmentQuestions = assessmentQuestions;
    }

    public void setHomework(String homework) {
        this.homework = homework;
    }

    public void setDetailedContent(String detailedContent) {
        this.detailedContent = detailedContent;
    }
}