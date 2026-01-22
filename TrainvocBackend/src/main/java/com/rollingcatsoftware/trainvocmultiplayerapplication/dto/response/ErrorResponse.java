package com.rollingcatsoftware.trainvocmultiplayerapplication.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Standardized error response DTO.
 * Provides consistent error format across all API endpoints.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private String error;
    private String message;
    private int status;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    /**
     * Create a simple error response with just a message.
     */
    public static ErrorResponse of(String message) {
        return ErrorResponse.builder()
                .error("Bad Request")
                .message(message)
                .status(400)
                .build();
    }

    /**
     * Create an error response with custom status.
     */
    public static ErrorResponse of(String error, String message, int status) {
        return ErrorResponse.builder()
                .error(error)
                .message(message)
                .status(status)
                .build();
    }
}
