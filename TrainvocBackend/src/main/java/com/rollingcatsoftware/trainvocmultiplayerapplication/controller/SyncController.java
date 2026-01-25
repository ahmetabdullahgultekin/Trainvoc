package com.rollingcatsoftware.trainvocmultiplayerapplication.controller;

import com.rollingcatsoftware.trainvocmultiplayerapplication.dto.response.ErrorResponse;
import com.rollingcatsoftware.trainvocmultiplayerapplication.dto.sync.BatchSyncRequest;
import com.rollingcatsoftware.trainvocmultiplayerapplication.dto.sync.BatchSyncResponse;
import com.rollingcatsoftware.trainvocmultiplayerapplication.dto.sync.SyncRequest;
import com.rollingcatsoftware.trainvocmultiplayerapplication.dto.sync.SyncResponse;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.User;
import com.rollingcatsoftware.trainvocmultiplayerapplication.security.CustomUserDetailsService;
import com.rollingcatsoftware.trainvocmultiplayerapplication.security.JwtTokenProvider;
import com.rollingcatsoftware.trainvocmultiplayerapplication.service.SyncService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for sync operations.
 * All endpoints require authentication.
 */
@RestController
@RequestMapping("/api/v1/sync")
@Tag(name = "Sync", description = "Data synchronization endpoints for offline-first clients")
@SecurityRequirement(name = "bearerAuth")
public class SyncController {

    private final SyncService syncService;
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;

    public SyncController(
            SyncService syncService,
            JwtTokenProvider jwtTokenProvider,
            CustomUserDetailsService userDetailsService) {
        this.syncService = syncService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Sync a batch of entities.
     */
    @PostMapping("/batch")
    @Operation(summary = "Batch sync", description = "Sync multiple entities in a single request")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Batch sync completed",
            content = @Content(schema = @Schema(implementation = BatchSyncResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> batchSync(
            @Valid @RequestBody BatchSyncRequest request,
            @RequestHeader("Authorization") String authHeader) {
        try {
            User user = getUserFromToken(authHeader);
            BatchSyncResponse response = syncService.processBatchSync(request, user);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(e.getMessage(), "UNAUTHORIZED"));
        }
    }

    /**
     * Sync a single word.
     */
    @PostMapping("/words")
    @Operation(summary = "Sync word", description = "Sync word learning progress")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Word synced",
            content = @Content(schema = @Schema(implementation = SyncResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<?> syncWord(
            @Valid @RequestBody SyncRequest request,
            @RequestHeader("Authorization") String authHeader) {
        return processSingleSync(request, authHeader, "word");
    }

    /**
     * Sync word statistics.
     */
    @PostMapping("/statistics")
    @Operation(summary = "Sync statistics", description = "Sync word learning statistics")
    public ResponseEntity<?> syncStatistic(
            @Valid @RequestBody SyncRequest request,
            @RequestHeader("Authorization") String authHeader) {
        return processSingleSync(request, authHeader, "statistic");
    }

    /**
     * Sync exam results.
     */
    @PostMapping("/exams")
    @Operation(summary = "Sync exam", description = "Sync exam completion and scores")
    public ResponseEntity<?> syncExam(
            @Valid @RequestBody SyncRequest request,
            @RequestHeader("Authorization") String authHeader) {
        return processSingleSync(request, authHeader, "exam");
    }

    /**
     * Sync achievement progress.
     */
    @PostMapping("/achievements")
    @Operation(summary = "Sync achievement", description = "Sync achievement progress and unlocks")
    public ResponseEntity<?> syncAchievement(
            @Valid @RequestBody SyncRequest request,
            @RequestHeader("Authorization") String authHeader) {
        return processSingleSync(request, authHeader, "achievement");
    }

    /**
     * Sync user profile.
     */
    @PostMapping("/profile")
    @Operation(summary = "Sync profile", description = "Sync user profile data")
    public ResponseEntity<?> syncUserProfile(
            @Valid @RequestBody SyncRequest request,
            @RequestHeader("Authorization") String authHeader) {
        return processSingleSync(request, authHeader, "userprofile");
    }

    /**
     * Sync backup data.
     */
    @PostMapping("/backup")
    @Operation(summary = "Sync backup", description = "Sync backup metadata and data")
    public ResponseEntity<?> syncBackup(
            @Valid @RequestBody SyncRequest request,
            @RequestHeader("Authorization") String authHeader) {
        return processSingleSync(request, authHeader, "backup");
    }

    /**
     * Sync feature flags.
     */
    @PostMapping("/feature-flags")
    @Operation(summary = "Sync feature flags", description = "Sync feature flag preferences")
    public ResponseEntity<?> syncFeatureFlag(
            @Valid @RequestBody SyncRequest request,
            @RequestHeader("Authorization") String authHeader) {
        return processSingleSync(request, authHeader, "featureflag");
    }

    /**
     * Get server changes since a timestamp.
     */
    @GetMapping("/changes")
    @Operation(summary = "Get changes", description = "Get server changes since a timestamp for bidirectional sync")
    public ResponseEntity<?> getChanges(
            @RequestParam long since,
            @RequestHeader("Authorization") String authHeader) {
        try {
            User user = getUserFromToken(authHeader);
            List<Map<String, Object>> changes = syncService.getServerChanges(user.getId(), since);
            return ResponseEntity.ok(Map.of(
                "changes", changes,
                "serverTime", System.currentTimeMillis()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(e.getMessage(), "UNAUTHORIZED"));
        }
    }

    /**
     * Get sync status for all entity types.
     */
    @GetMapping("/status")
    @Operation(summary = "Get sync status", description = "Get last sync timestamps for all entity types")
    public ResponseEntity<?> getSyncStatus(
            @RequestHeader("Authorization") String authHeader) {
        try {
            User user = getUserFromToken(authHeader);
            Map<String, Object> syncStatus = syncService.getSyncStatus(user);
            return ResponseEntity.ok(syncStatus);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(e.getMessage(), "UNAUTHORIZED"));
        }
    }

    // ============ Helper Methods ============

    private ResponseEntity<?> processSingleSync(
            SyncRequest request,
            String authHeader,
            String expectedType) {
        try {
            User user = getUserFromToken(authHeader);
            SyncResponse response = syncService.processSingleSync(request, user);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(e.getMessage(), "UNAUTHORIZED"));
        }
    }

    private User getUserFromToken(String authHeader) {
        String token = jwtTokenProvider.resolveToken(authHeader);
        if (token == null || !jwtTokenProvider.validateToken(token)) {
            throw new IllegalArgumentException("Invalid or missing token");
        }

        if (!jwtTokenProvider.isUserToken(token)) {
            throw new IllegalArgumentException("Invalid token type");
        }

        Long userId = jwtTokenProvider.getUserId(token);
        return userDetailsService.loadUserById(userId);
    }
}
