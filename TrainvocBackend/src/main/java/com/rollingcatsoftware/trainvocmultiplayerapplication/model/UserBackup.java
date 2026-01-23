package com.rollingcatsoftware.trainvocmultiplayerapplication.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity for tracking user's backup metadata.
 * Actual backup data is stored in cloud storage, this tracks metadata.
 */
@Entity
@Table(name = "user_backups", indexes = {
    @Index(name = "idx_ub_user_id", columnList = "user_id"),
    @Index(name = "idx_ub_created_at", columnList = "created_at")
})
public class UserBackup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "backup_id", nullable = false, length = 100, unique = true)
    private String backupId;

    @Column(name = "storage_path", length = 500)
    private String storagePath;

    @Column(name = "size_bytes")
    private long sizeBytes;

    @Column(length = 64)
    private String checksum;

    @Column(name = "backup_type", length = 50)
    private String backupType = "FULL";

    @Column(name = "is_encrypted", nullable = false)
    private boolean isEncrypted = true;

    @Column(length = 20)
    private String status = "PENDING";

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    // Constructors
    public UserBackup() {
        this.createdAt = LocalDateTime.now();
    }

    public UserBackup(User user, String backupId) {
        this();
        this.user = user;
        this.backupId = backupId;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getBackupId() { return backupId; }
    public void setBackupId(String backupId) { this.backupId = backupId; }

    public String getStoragePath() { return storagePath; }
    public void setStoragePath(String storagePath) { this.storagePath = storagePath; }

    public long getSizeBytes() { return sizeBytes; }
    public void setSizeBytes(long sizeBytes) { this.sizeBytes = sizeBytes; }

    public String getChecksum() { return checksum; }
    public void setChecksum(String checksum) { this.checksum = checksum; }

    public String getBackupType() { return backupType; }
    public void setBackupType(String backupType) { this.backupType = backupType; }

    public boolean isEncrypted() { return isEncrypted; }
    public void setEncrypted(boolean encrypted) { isEncrypted = encrypted; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    // Status constants
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_IN_PROGRESS = "IN_PROGRESS";
    public static final String STATUS_COMPLETED = "COMPLETED";
    public static final String STATUS_FAILED = "FAILED";
}
