package com.rollingcatsoftware.trainvocmultiplayerapplication.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity for tracking user's word statistics (correct/wrong counts).
 */
@Entity
@Table(name = "user_word_statistics", indexes = {
    @Index(name = "idx_uws_user_id", columnList = "user_id"),
    @Index(name = "idx_uws_word_id", columnList = "word_id")
}, uniqueConstraints = {
    @UniqueConstraint(name = "uk_user_word_stat", columnNames = {"user_id", "word_id"})
})
public class UserWordStatistic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "word_id", nullable = false, length = 100)
    private String wordId;

    @Column(name = "correct_count", nullable = false)
    private int correctCount = 0;

    @Column(name = "wrong_count", nullable = false)
    private int wrongCount = 0;

    @Column(name = "skipped_count", nullable = false)
    private int skippedCount = 0;

    @Column(name = "last_answered_at")
    private LocalDateTime lastAnsweredAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public UserWordStatistic() {
        this.createdAt = LocalDateTime.now();
    }

    public UserWordStatistic(User user, String wordId) {
        this();
        this.user = user;
        this.wordId = wordId;
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

    public String getWordId() { return wordId; }
    public void setWordId(String wordId) { this.wordId = wordId; }

    public int getCorrectCount() { return correctCount; }
    public void setCorrectCount(int correctCount) { this.correctCount = correctCount; }

    public int getWrongCount() { return wrongCount; }
    public void setWrongCount(int wrongCount) { this.wrongCount = wrongCount; }

    public int getSkippedCount() { return skippedCount; }
    public void setSkippedCount(int skippedCount) { this.skippedCount = skippedCount; }

    public LocalDateTime getLastAnsweredAt() { return lastAnsweredAt; }
    public void setLastAnsweredAt(LocalDateTime lastAnsweredAt) { this.lastAnsweredAt = lastAnsweredAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // Helper methods
    public int getTotalAttempts() {
        return correctCount + wrongCount + skippedCount;
    }

    public double getAccuracy() {
        int attempts = correctCount + wrongCount;
        return attempts > 0 ? (double) correctCount / attempts * 100 : 0;
    }
}
