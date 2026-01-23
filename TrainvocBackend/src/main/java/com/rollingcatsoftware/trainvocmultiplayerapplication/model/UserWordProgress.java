package com.rollingcatsoftware.trainvocmultiplayerapplication.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity for tracking user's word learning progress.
 * Stores spaced repetition data for each word per user.
 */
@Entity
@Table(name = "user_word_progress", indexes = {
    @Index(name = "idx_uwp_user_id", columnList = "user_id"),
    @Index(name = "idx_uwp_word", columnList = "word"),
    @Index(name = "idx_uwp_next_review", columnList = "next_review_date")
}, uniqueConstraints = {
    @UniqueConstraint(name = "uk_user_word", columnNames = {"user_id", "word"})
})
public class UserWordProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 100)
    private String word;

    @Column(name = "last_reviewed")
    private LocalDateTime lastReviewed;

    @Column(name = "next_review_date")
    private LocalDateTime nextReviewDate;

    @Column(name = "easiness_factor", nullable = false)
    private float easinessFactor = 2.5f;

    @Column(name = "interval_days", nullable = false)
    private int intervalDays = 0;

    @Column(nullable = false)
    private int repetitions = 0;

    @Column(name = "is_favorite", nullable = false)
    private boolean isFavorite = false;

    @Column(name = "seconds_spent", nullable = false)
    private int secondsSpent = 0;

    @Column(name = "is_learned", nullable = false)
    private boolean isLearned = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public UserWordProgress() {
        this.createdAt = LocalDateTime.now();
    }

    public UserWordProgress(User user, String word) {
        this();
        this.user = user;
        this.word = word;
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

    public String getWord() { return word; }
    public void setWord(String word) { this.word = word; }

    public LocalDateTime getLastReviewed() { return lastReviewed; }
    public void setLastReviewed(LocalDateTime lastReviewed) { this.lastReviewed = lastReviewed; }

    public LocalDateTime getNextReviewDate() { return nextReviewDate; }
    public void setNextReviewDate(LocalDateTime nextReviewDate) { this.nextReviewDate = nextReviewDate; }

    public float getEasinessFactor() { return easinessFactor; }
    public void setEasinessFactor(float easinessFactor) { this.easinessFactor = easinessFactor; }

    public int getIntervalDays() { return intervalDays; }
    public void setIntervalDays(int intervalDays) { this.intervalDays = intervalDays; }

    public int getRepetitions() { return repetitions; }
    public void setRepetitions(int repetitions) { this.repetitions = repetitions; }

    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }

    public int getSecondsSpent() { return secondsSpent; }
    public void setSecondsSpent(int secondsSpent) { this.secondsSpent = secondsSpent; }

    public boolean isLearned() { return isLearned; }
    public void setLearned(boolean learned) { isLearned = learned; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
