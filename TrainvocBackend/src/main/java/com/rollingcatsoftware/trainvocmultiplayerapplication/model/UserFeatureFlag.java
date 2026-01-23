package com.rollingcatsoftware.trainvocmultiplayerapplication.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity for storing user-specific feature flag overrides.
 */
@Entity
@Table(name = "user_feature_flags", indexes = {
    @Index(name = "idx_uff_user_id", columnList = "user_id"),
    @Index(name = "idx_uff_flag_name", columnList = "flag_name")
}, uniqueConstraints = {
    @UniqueConstraint(name = "uk_user_flag", columnNames = {"user_id", "flag_name"})
})
public class UserFeatureFlag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "flag_name", nullable = false, length = 100)
    private String flagName;

    @Column(name = "is_enabled", nullable = false)
    private boolean isEnabled = false;

    @Column(length = 50)
    private String variant;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public UserFeatureFlag() {
        this.createdAt = LocalDateTime.now();
    }

    public UserFeatureFlag(User user, String flagName, boolean isEnabled) {
        this();
        this.user = user;
        this.flagName = flagName;
        this.isEnabled = isEnabled;
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

    public String getFlagName() { return flagName; }
    public void setFlagName(String flagName) { this.flagName = flagName; }

    public boolean isEnabled() { return isEnabled; }
    public void setEnabled(boolean enabled) { isEnabled = enabled; }

    public String getVariant() { return variant; }
    public void setVariant(String variant) { this.variant = variant; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
