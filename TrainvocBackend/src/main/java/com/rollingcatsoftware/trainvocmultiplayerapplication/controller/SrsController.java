package com.rollingcatsoftware.trainvocmultiplayerapplication.controller;

import com.rollingcatsoftware.trainvocmultiplayerapplication.dto.response.ErrorResponse;
import com.rollingcatsoftware.trainvocmultiplayerapplication.dto.srs.SrsReviewBatchRequest;
import com.rollingcatsoftware.trainvocmultiplayerapplication.dto.srs.SrsScheduleResponse;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.User;
import com.rollingcatsoftware.trainvocmultiplayerapplication.security.CustomUserDetailsService;
import com.rollingcatsoftware.trainvocmultiplayerapplication.security.JwtTokenProvider;
import com.rollingcatsoftware.trainvocmultiplayerapplication.service.SrsService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

/**
 * REST endpoints for the spaced-repetition (SRS) engine's cross-device sync
 * slice (S4 of {@code docs/design/srs-spaced-repetition-engine.md}).
 *
 * <p>The FSRS scheduling algorithm runs on the Android client; these endpoints
 * only persist and serve the resulting schedule so it can survive a re-install
 * or follow the user to a second device. All endpoints require authentication.</p>
 */
@RestController
@RequestMapping("/api/v1/srs")
@Tag(name = "SRS", description = "Spaced-repetition schedule sync (FSRS, cross-device)")
@SecurityRequirement(name = "bearerAuth")
public class SrsController {

    private final SrsService srsService;
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;

    public SrsController(SrsService srsService,
                         JwtTokenProvider jwtTokenProvider,
                         CustomUserDetailsService userDetailsService) {
        this.srsService = srsService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Batch-upserts the client's dirty schedule rows. Fire-and-forget from the
     * client's perspective; conflicts resolve last-write-wins on clientUpdatedAt.
     */
    @PostMapping("/reviews")
    @Operation(summary = "Batch upsert review schedule",
            description = "Persists up to 500 client schedule rows (last-write-wins on clientUpdatedAt).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Schedule rows accepted"),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> upsertReviews(
            @Valid @RequestBody SrsReviewBatchRequest request,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String userId = resolveUserId(authHeader);
            srsService.upsertReviews(userId, request);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(e.getMessage(), "UNAUTHORIZED"));
        }
    }

    /**
     * Returns the user's full schedule plus aggregate due stats, used to seed a
     * freshly-installed client.
     */
    @GetMapping("/schedule")
    @Operation(summary = "Pull full review schedule",
            description = "Returns the user's complete schedule and due counts to seed a new device.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Schedule returned",
                    content = @Content(schema = @Schema(implementation = SrsScheduleResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> getSchedule(@RequestHeader("Authorization") String authHeader) {
        try {
            String userId = resolveUserId(authHeader);
            SrsScheduleResponse response = srsService.getSchedule(userId, Instant.now());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(e.getMessage(), "UNAUTHORIZED"));
        }
    }

    /**
     * Resolves the authenticated user from the bearer token and returns a stable
     * string id for schedule scoping. Mirrors {@code SyncController}.
     */
    private String resolveUserId(String authHeader) {
        String token = jwtTokenProvider.resolveToken(authHeader);
        if (token == null || !jwtTokenProvider.validateToken(token)) {
            throw new IllegalArgumentException("Invalid or missing token");
        }
        if (!jwtTokenProvider.isUserToken(token)) {
            throw new IllegalArgumentException("Invalid token type");
        }
        Long userId = jwtTokenProvider.getUserId(token);
        User user = userDetailsService.loadUserById(userId);
        return String.valueOf(user.getId());
    }
}
