package com.example.bankcards.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(name = "CreateUserRequest", description = "Request to create a new user")
public record CreateUserRequest(
        @JsonProperty("username")
        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 16, message = "Username must be between 3 and 16 characters")
        @Schema(description = "Username for the new user", example = "test_user")
        String username,

        @JsonProperty("password")
        @NotBlank(message = "Password is required")
        @Size(min = 6, max = 24, message = "Password must be between 6 and 24 characters")
        @Schema(description = "Password for the new user", example = "userpass")
        String password,

        @JsonProperty("role")
        @NotNull(message = "Role is required")
        @Schema(description = "Role assigned to the new user", example = "ROLE_USER")
        Role role
) {}