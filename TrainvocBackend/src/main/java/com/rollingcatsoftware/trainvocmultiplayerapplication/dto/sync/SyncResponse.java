package com.rollingcatsoftware.trainvocmultiplayerapplication.dto.sync;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO for sync operations.
 */
public record SyncResponse(
    boolean success,
    String message,
    String entityType,
    String entityId,
    LocalDateTime syncedAt,
    List<ConflictInfo> conflicts
) {
    /**
     * Creates a successful sync response.
     */
    public static SyncResponse success(String entityType, String entityId) {
        return new SyncResponse(
            true,
            "Sync successful",
            entityType,
            entityId,
            LocalDateTime.now(),
            List.of()
        );
    }

    /**
     * Creates a failed sync response.
     */
    public static SyncResponse failure(String entityType, String entityId, String message) {
        return new SyncResponse(
            false,
            message,
            entityType,
            entityId,
            LocalDateTime.now(),
            List.of()
        );
    }

    /**
     * Creates a sync response with conflicts.
     */
    public static SyncResponse withConflicts(String entityType, String entityId, List<ConflictInfo> conflicts) {
        return new SyncResponse(
            false,
            "Sync conflict detected",
            entityType,
            entityId,
            LocalDateTime.now(),
            conflicts
        );
    }

    /**
     * Conflict information for sync operations.
     */
    public record ConflictInfo(
        String field,
        Object localValue,
        Object serverValue,
        long localTimestamp,
        long serverTimestamp
    ) {}
}
