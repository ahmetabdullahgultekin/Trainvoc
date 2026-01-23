package com.rollingcatsoftware.trainvocmultiplayerapplication.dto.auth;

import jakarta.validation.constraints.NotBlank;

/**
 * Request DTO for user login.
 * Identifier can be either username or email.
 */
public record LoginRequest(
    @NotBlank(message = "Username or email is required")
    String identifier,

    @NotBlank(message = "Password is required")
    String password
) {}
