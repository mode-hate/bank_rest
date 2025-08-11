package com.example.bankcards.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


@JsonIgnoreProperties(ignoreUnknown = true)
public record AuthRequest(
        @JsonProperty("username")
        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 16, message = "Username must be between 3 and 16 characters")
        String username,

        @JsonProperty("password")
        @NotBlank(message = "Password is required")
        @Size(min = 6, max = 24, message = "Password must be between 6 and 24 characters")
        String password
) {}
