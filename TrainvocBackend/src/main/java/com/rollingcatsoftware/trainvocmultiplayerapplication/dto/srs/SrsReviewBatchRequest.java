package com.rollingcatsoftware.trainvocmultiplayerapplication.dto.srs;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * Request body for {@code POST /api/v1/srs/reviews} — a batch upsert of the
 * client's dirty schedule rows.
 *
 * <p>Per the design doc, batch size is capped at 500 rows per request.</p>
 */
public record SrsReviewBatchRequest(
        @NotEmpty(message = "reviews list must not be empty")
        @Size(max = 500, message = "Maximum 500 reviews per batch")
        @Valid
        List<SrsReviewItem> reviews
) {}
