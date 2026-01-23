package com.rollingcatsoftware.trainvocmultiplayerapplication.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity for tracking user's exam/quiz history.
 */
@Entity
@Table(name = "user_exam_history", indexes = {
    @Index(name = "idx_ueh_user_id", columnList = "user_id"),
    @Index(name = "idx_ueh_completed_at", columnList = "completed_at")
})
public class UserExamHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "exam_id", nullable = false, length = 100)
    private String examId;

    @Column(name = "exam_type", length = 50)
    private String examType;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(nullable = false)
    private int score = 0;

    @Column(name = "total_questions", nullable = false)
    private int totalQuestions = 0;

    @Column(name = "correct_answers", nullable = false)
    private int correctAnswers = 0;

    @Column(name = "time_spent_seconds")
    private int timeSpentSeconds = 0;

    @Column(length = 20)
    private String level;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public UserExamHistory() {
        this.createdAt = LocalDateTime.now();
    }

    public UserExamHistory(User user, String examId) {
        this();
        this.user = user;
        this.examId = examId;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getExamId() { return examId; }
    public void setExamId(String examId) { this.examId = examId; }

    public String getExamType() { return examType; }
    public void setExamType(String examType) { this.examType = examType; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public int getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(int totalQuestions) { this.totalQuestions = totalQuestions; }

    public int getCorrectAnswers() { return correctAnswers; }
    public void setCorrectAnswers(int correctAnswers) { this.correctAnswers = correctAnswers; }

    public int getTimeSpentSeconds() { return timeSpentSeconds; }
    public void setTimeSpentSeconds(int timeSpentSeconds) { this.timeSpentSeconds = timeSpentSeconds; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // Alias for durationSeconds (maps to timeSpentSeconds)
    public int getDurationSeconds() { return timeSpentSeconds; }
    public void setDurationSeconds(int durationSeconds) { this.timeSpentSeconds = durationSeconds; }

    // Helper methods
    public double getAccuracy() {
        return totalQuestions > 0 ? (double) correctAnswers / totalQuestions * 100 : 0;
    }
}
