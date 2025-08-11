package com.example.bankcards.dto.card;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;


@JsonIgnoreProperties(ignoreUnknown = true)
public record CreateCardRequest(
        @JsonProperty("cardNumber")
        @NotBlank(message = "Card number is required")
        @Pattern(regexp = "\\d{16}", message = "Card number must contain exactly 16 digits")
        String cardNumber,

        @JsonProperty("owner")
        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 16, message = "Username must be between 3 and 16 characters")
        String owner,

        @JsonProperty("balance")
        @NotNull(message = "Balance is required")
        @Digits(integer = 10, fraction = 2, message = "Balance must have up to 10 digits before decimal point and 2 digits after")
        @DecimalMin(value = "0.01", message = "Balance must be greater than 0")
        BigDecimal balance
) {}