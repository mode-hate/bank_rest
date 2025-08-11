package com.example.bankcards.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UpdateUserRoleRequest(

        @JsonProperty("role")
        @NotNull(message = "Role is required")
        Role role
) {}
