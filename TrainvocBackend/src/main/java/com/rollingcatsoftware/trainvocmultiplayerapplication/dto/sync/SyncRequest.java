package com.rollingcatsoftware.trainvocmultiplayerapplication.dto.sync;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;

/**
 * Base sync request containing common fields for all sync operations.
 */
public record SyncRequest(
    @NotBlank(message = "Entity type is required")
    String entityType,

    @NotBlank(message = "Entity ID is required")
    String entityId,

    @NotNull(message = "Data is required")
    Map<String, Object> data,

    long timestamp,

    String action // CREATE, UPDATE, DELETE
) {
    public enum Action {
        CREATE, UPDATE, DELETE
    }
}
