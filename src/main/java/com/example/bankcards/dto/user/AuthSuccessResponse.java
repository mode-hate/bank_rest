package com.example.bankcards.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "JWT token response")
public record AuthSuccessResponse(
        @Schema(example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String token
) {}