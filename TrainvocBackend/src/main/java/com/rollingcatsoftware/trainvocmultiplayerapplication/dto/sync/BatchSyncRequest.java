package com.rollingcatsoftware.trainvocmultiplayerapplication.dto.sync;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * Batch sync request for multiple entities.
 */
public record BatchSyncRequest(
    @NotEmpty(message = "Items list cannot be empty")
    @Size(max = 100, message = "Maximum 100 items per batch")
    @Valid
    List<SyncRequest> items,

    long clientTimestamp,

    String deviceId
) {}
