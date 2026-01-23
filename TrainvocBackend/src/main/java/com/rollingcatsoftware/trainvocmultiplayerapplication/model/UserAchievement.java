package com.rollingcatsoftware.trainvocmultiplayerapplication.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity for tracking user's achievement progress.
 */
@Entity
@Table(name = "user_achievements", indexes = {
    @Index(name = "idx_ua_user_id", columnList = "user_id"),
    @Index(name = "idx_ua_achievement_id", columnList = "achievement_id")
}, uniqueConstraints = {
    @UniqueConstraint(name = "uk_user_achievement", columnNames = {"user_id", "achievement_id"})
})
public class UserAchievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "achievement_id", nullable = false, length = 100)
    private String achievementId;

    @Column(nullable = false)
    private int progress = 0;

    @Column(name = "max_progress", nullable = false)
    private int maxProgress = 100;

    @Column(name = "is_unlocked", nullable = false)
    private boolean isUnlocked = false;

    @Column(name = "unlocked_at")
    private LocalDateTime unlockedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public UserAchievement() {
        this.createdAt = LocalDateTime.now();
    }

    public UserAchievement(User user, String achievementId) {
        this();
        this.user = user;
        this.achievementId = achievementId;
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

    public String getAchievementId() { return achievementId; }
    public void setAchievementId(String achievementId) { this.achievementId = achievementId; }

    public int getProgress() { return progress; }
    public void setProgress(int progress) {
        this.progress = progress;
        if (progress >= maxProgress && !isUnlocked) {
            this.isUnlocked = true;
            this.unlockedAt = LocalDateTime.now();
        }
    }

    public int getMaxProgress() { return maxProgress; }
    public void setMaxProgress(int maxProgress) { this.maxProgress = maxProgress; }

    // Alias for targetProgress (maps to maxProgress)
    public int getTargetProgress() { return maxProgress; }
    public void setTargetProgress(int targetProgress) { this.maxProgress = targetProgress; }

    public boolean isUnlocked() { return isUnlocked; }
    public void setUnlocked(boolean unlocked) {
        isUnlocked = unlocked;
        if (unlocked && unlockedAt == null) {
            unlockedAt = LocalDateTime.now();
        }
    }

    public LocalDateTime getUnlockedAt() { return unlockedAt; }
    public void setUnlockedAt(LocalDateTime unlockedAt) { this.unlockedAt = unlockedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // Helper methods
    public double getProgressPercentage() {
        return maxProgress > 0 ? (double) progress / maxProgress * 100 : 0;
    }
}
