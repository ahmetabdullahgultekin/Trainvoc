package com.rollingcatsoftware.trainvocmultiplayerapplication.dto.srs;

import java.util.List;

/**
 * Response body for {@code GET /api/v1/srs/schedule} — the user's full schedule,
 * used to seed a freshly-installed client.
 *
 * @param schedule  all schedule rows for the user, soonest-due first
 * @param totalDue  number of rows due now (dueAt &lt;= server time)
 * @param nextDueAt epoch-ms of the soonest upcoming review, or {@code null} if empty
 */
public record SrsScheduleResponse(
        List<SrsReviewItem> schedule,
        long totalDue,
        Long nextDueAt
) {}
