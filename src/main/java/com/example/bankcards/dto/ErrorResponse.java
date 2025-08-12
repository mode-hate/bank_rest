package com.example.bankcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ErrorResponse", description = "Standard error response")
public record ErrorResponse(
        @Schema(description = "Error type", example = "ValidationFailed")
        String error,

        @Schema(description = "Detailed error message", example = "Username must not be blank")
        String message
) {}