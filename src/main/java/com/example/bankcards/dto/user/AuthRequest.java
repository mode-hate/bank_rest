package com.example.bankcards.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Login request")
@JsonIgnoreProperties(ignoreUnknown = true)
public record AuthRequest(
        @JsonProperty("username")
        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 16, message = "Username must be between 3 and 16 characters")
        @Schema(example = "test_admin")
        String username,

        @JsonProperty("password")
        @NotBlank(message = "Password is required")
        @Size(min = 6, max = 24, message = "Password must be between 6 and 24 characters")
        @Schema(example = "adminpass")
        String password
) {}
