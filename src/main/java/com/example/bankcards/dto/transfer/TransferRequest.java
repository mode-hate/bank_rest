package com.example.bankcards.dto.transfer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TransferRequest(

        @JsonProperty("fromCardId")
        @NotNull(message = "fromCardId is required")
        Long fromCardId,

        @JsonProperty("toCardId")
        @NotNull(message = "toCardId is required")
        Long toCardId,

        @JsonProperty("amount")
        @NotNull(message = "Amount is required")
        @Digits(integer = 10, fraction = 2, message = "Amount must have up to 10 digits before decimal point and 2 digits after")
        @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
        BigDecimal amount,

        @JsonProperty("description")
        @Size(min = 5, max = 250, message = "Description must be between 5 and 250 characters, if present")
        String description
) {}