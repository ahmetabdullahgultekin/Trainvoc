package com.rollingcatsoftware.trainvocmultiplayerapplication.dto.sync;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO for batch sync operations.
 */
public record BatchSyncResponse(
    int totalItems,
    int successCount,
    int failureCount,
    int conflictCount,
    LocalDateTime syncedAt,
    List<SyncResponse> results
) {
    /**
     * Creates a batch sync response from individual results.
     */
    public static BatchSyncResponse fromResults(List<SyncResponse> results) {
        int successCount = (int) results.stream().filter(SyncResponse::success).count();
        int conflictCount = (int) results.stream()
            .filter(r -> !r.success() && !r.conflicts().isEmpty())
            .count();
        int failureCount = results.size() - successCount - conflictCount;

        return new BatchSyncResponse(
            results.size(),
            successCount,
            failureCount,
            conflictCount,
            LocalDateTime.now(),
            results
        );
    }
}
